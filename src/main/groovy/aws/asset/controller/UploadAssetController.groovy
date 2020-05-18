package aws.asset.controller

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import aws.asset.model.asset.UploadAssetRequest
import aws.asset.service.asset.UploadAssetService

import aws.asset.model.exception.ConflictException
import aws.asset.model.exception.DataNotFoundException
import aws.asset.model.exception.ValidationException
import aws.asset.service.asset.UploadAssetService

@Controller
@RequestMapping("/assets")
class UploadAssetController {
	
	@Autowired UploadAssetService uploadAssetService
	
	@RequestMapping(value="/{objectId}", method=RequestMethod.GET)
	@ResponseBody Object getObject(@PathVariable("objectId") String objectId) {
		
		uploadAssetService.getObject(UUID.fromString(objectId))
	}
	
	@RequestMapping(value="getUploadDetails", method=RequestMethod.POST)
	@ResponseBody Object getUploadDetailsForAsset(@RequestBody UploadAssetRequest uploadAssetRequest) {
		
		
		def checkResults = uploadAssetService.searchObject(uploadAssetRequest)
		
		if(checkResults) {
			throw new ConflictException("Object with this file name already exists!")
		}
		
		def queryResults = uploadAssetService.getUploadDetails(uploadAssetRequest)
			
		queryResults
	}
	
	@RequestMapping(value="/updateUploadStatus/{objectId}", method=RequestMethod.PUT)
	@ResponseBody Object updateUploadStatus(@PathVariable("objectId") String objectId, @RequestBody UploadAssetRequest uploadAssetRequest) {
		
		def checkResults = uploadAssetService.getObject(UUID.fromString(objectId))
		
		if(!checkResults) {
			throw new DataNotFoundException("Object with this ID does not exist")
		}
		
		if(uploadAssetRequest.statusFlag.toLowerCase() != 'uploaded') {
			throw new ValidationException("Invalid status flag")
		}
		
		def queryResults = uploadAssetService.updateObject(UUID.fromString(objectId), uploadAssetRequest)		
		
		["status" : "Upload completed"]
	}
	
	@RequestMapping(value="/getDownloadDetails/{objectId}", method=RequestMethod.GET)
	@ResponseBody Object getDownloadDetails(@PathVariable("objectId") String objectId, @RequestBody UploadAssetRequest uploadAssetRequest) {
		
		def checkResults = uploadAssetService.getObject(UUID.fromString(objectId))
		
		if(checkResults.statusFlag != 'Uploaded') {
			throw new ConflictException("Can't download object. Upload is not yet completed")
		}
		def queryResults = uploadAssetService.getDownloadDetails(UUID.fromString(objectId), uploadAssetRequest)
	
		queryResults
	}
	
}
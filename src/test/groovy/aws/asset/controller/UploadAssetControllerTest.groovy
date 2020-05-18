package aws.asset.controller

import static org.junit.Assert.*
import static org.junit.matchers.JUnitMatchers.*
import static org.mockito.Mockito.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*
import groovy.json.JsonBuilder

import org.junit.Test
import org.springframework.http.MediaType

import aws.asset.controller.UploadAssetController
import aws.asset.model.asset.UploadAsset
import aws.asset.model.asset.UploadAssetRequest
import aws.asset.service.asset.UploadAssetServiceImpl

import java.net.URL;

class UploadAssetControllerTest extends BaseControllerTestClass{
	
	@Test
	public void getUploadDetails() throws Exception {
		
		def mockUploadAssetService = mock(UploadAssetServiceImpl.class)
		when(mockUploadAssetService.searchObject(anyObject())).thenReturn(null)
		when(mockUploadAssetService.getUploadDetails(anyObject())).thenReturn([uniqueId: "test", uploadUrl: "test"])
		wac.getBean(UploadAssetController.class).setUploadAssetService(mockUploadAssetService)
		
		def uploadAssetRequest = new UploadAssetRequest(fileName: "test.txt")
		def uploadAssetRequestJson = new JsonBuilder(uploadAssetRequest).toString()
		
		this.mockMvc.perform(post("/assets/getUploadDetails").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(uploadAssetRequestJson))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString('uploadUrl')))
	}
	
	@Test
	public void getUploadDetails_invalid() throws Exception {
		def object_id = UUID.randomUUID()
		def mockUploadAssetService = mock(UploadAssetServiceImpl.class)
		when(mockUploadAssetService.searchObject(anyObject())).thenReturn(new UploadAsset(assetId: object_id, fileName: "test.txt", statusFlag: "Pending"))
		when(mockUploadAssetService.getUploadDetails(anyObject())).thenReturn([uniqueId: "test", uploadUrl: "test"])
		wac.getBean(UploadAssetController.class).setUploadAssetService(mockUploadAssetService)
		
		def uploadAssetRequest = new UploadAssetRequest(fileName: "test.txt")
		def uploadAssetRequestJson = new JsonBuilder(uploadAssetRequest).toString()
		
		this.mockMvc.perform(post("/assets/getUploadDetails").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(uploadAssetRequestJson))
			.andExpect(status().isConflict())
	}
	
	@Test
	public void updateUploadStatus() throws Exception {
		def object_id = UUID.randomUUID()
		def mockUploadAssetService = mock(UploadAssetServiceImpl.class)
		when(mockUploadAssetService.getObject(anyObject())).thenReturn(new UploadAsset(assetId: object_id, fileName: "test", statusFlag: "test"))
		when(mockUploadAssetService.updateObject(anyObject(), anyObject())).thenReturn(true)
		wac.getBean(UploadAssetController.class).setUploadAssetService(mockUploadAssetService)
		
		def uploadAssetRequest = new UploadAssetRequest(fileName: "test.txt")
		def uploadAssetRequestJson = new JsonBuilder(uploadAssetRequest).toString()
		
		this.mockMvc.perform(post("/updateUploadStatus/${object_id}").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(uploadAssetRequestJson))
			.andExpect(status().isOk())

	}
	
	@Test
	public void updateUploadStatus_invalid() throws Exception {
		def object_id = UUID.randomUUID()
		def mockUploadAssetService = mock(UploadAssetServiceImpl.class)
		when(mockUploadAssetService.getObject(anyObject())).thenReturn(null)
		when(mockUploadAssetService.updateObject(anyObject(), anyObject())).thenReturn(true)
		wac.getBean(UploadAssetController.class).setUploadAssetService(mockUploadAssetService)
		
		def uploadAssetRequest = new UploadAssetRequest(statusFlag: "Completed")
		def uploadAssetRequestJson = new JsonBuilder(uploadAssetRequest).toString()
		
		this.mockMvc.perform(post("/updateUploadStatus/217a4c14-16a1-488d-a1f2-ce4340892fff").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(uploadAssetRequestJson))
			.andExpect(status().isNotFound())

	}
	
	@Test
	public void getDownloadDetails() throws Exception {
		def object_id = UUID.randomUUID()
		def mockUploadAssetService = mock(UploadAssetServiceImpl.class)
		when(mockUploadAssetService.getObject(anyObject())).thenReturn(new UploadAsset(assetId: object_id, fileName: "test", statusFlag: "Completed"))
		when(mockUploadAssetService.getDownloadDetails(anyObject(), anyObject())).thenReturn([downloadUrl: "test"])
		wac.getBean(UploadAssetController.class).setUploadAssetService(mockUploadAssetService)
		
		def uploadAssetRequest = new UploadAssetRequest(timeout: "3600")
		def uploadAssetRequestJson = new JsonBuilder(uploadAssetRequest).toString()
		
		this.mockMvc.perform(post("/assets/getUploadDetails").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(uploadAssetRequestJson))
			.andExpect(status().isOk())
	}
	
	@Test
	public void getDownloadDetails_invalid() throws Exception {
		def object_id = UUID.randomUUID()
		def mockUploadAssetService = mock(UploadAssetServiceImpl.class)
		when(mockUploadAssetService.getObject(anyObject())).thenReturn(new UploadAsset(assetId: object_id, fileName: "test", statusFlag: "Pending"))
		when(mockUploadAssetService.getDownloadDetails(anyObject(), anyObject())).thenReturn([downloadUrl: "test"])
		wac.getBean(UploadAssetController.class).setUploadAssetService(mockUploadAssetService)
		
		def uploadAssetRequest = new UploadAssetRequest(timeout: "3600")
		def uploadAssetRequestJson = new JsonBuilder(uploadAssetRequest).toString()
		
		this.mockMvc.perform(post("/assets/getUploadDetails").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(uploadAssetRequestJson))
			.andExpect(status().isConflict())
	}
}
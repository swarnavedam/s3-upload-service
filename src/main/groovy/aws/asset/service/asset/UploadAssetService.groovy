package aws.asset.service.asset

import java.net.URL;

import aws.asset.model.asset.UploadAsset
import aws.asset.model.asset.UploadAssetRequest

interface UploadAssetService {

	 def getUploadDetails(UploadAssetRequest uploadAssetRequest)
	 UploadAsset getObject(UUID objectId)
	 def updateObject(UUID objectId, UploadAssetRequest uploadAssetRequest)
	 def getDownloadDetails(UUID objectId, UploadAssetRequest uploadAssetRequest)
	 def searchObject(UploadAssetRequest uploadAssetRequest)
}

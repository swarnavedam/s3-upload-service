package aws.asset.service.asset

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.cassandra.core.CassandraOperations

import com.datastax.driver.core.ConsistencyLevel
import com.datastax.driver.core.querybuilder.Insert
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.driver.core.querybuilder.Select
import com.datastax.driver.core.querybuilder.Update

import aws.asset.model.asset.UploadAsset
import aws.asset.model.asset.UploadAssetRequest
import aws.asset.util.AwsS3Utils

class UploadAssetServiceImpl implements UploadAssetService {
	
	Logger logger = LogManager.getLogger(UploadAssetServiceImpl.class)
	
	String bucketName = "swarna-storage"
	String keyspace = "aws"
	String table = "assets"
	@Autowired CassandraOperations cassandraOps
	@Autowired AwsS3Utils awsS3Utils
	ConsistencyLevel consistencyLevel = ConsistencyLevel.LOCAL_QUORUM
	def limitRowsFromCassandra = 1000
	
	@Override
	public def getUploadDetails(UploadAssetRequest uploadAssetRequest) {
		
		def object_id = UUID.randomUUID()
		
		Insert assetsInsert = QueryBuilder.insertInto(keyspace, table)
		assetsInsert.value("asset_id", object_id)
		assetsInsert.value("file_name", uploadAssetRequest.fileName)
		assetsInsert.value("status_flag", "Pending")
		cassandraOps.getCqlOperations()?.execute(assetsInsert)
		
		def presignedUrl = awsS3Utils.generateUrlToPutObject(bucketName, uploadAssetRequest.fileName)
		
		def results = [uniqueId: object_id, uploadUrl: presignedUrl]
		
		results
	}
	
	@Override
	public UploadAsset getObject(UUID objectId) {
		
		Select selectQuery = QueryBuilder.select().from(keyspace, table)
		selectQuery.where(QueryBuilder.eq("asset_id", objectId))
		selectQuery.setConsistencyLevel(consistencyLevel)
		def result
		try{
			logger.info("SELECT QUERY: ${selectQuery}")
			result = cassandraOps.select(selectQuery, UploadAsset.class)
		} catch(ex){
			ex.printStackTrace()
		}
		
		result[0]
	}
	
	@Override
	public def updateObject(UUID objectId, UploadAssetRequest uploadAssetRequest) {
		
		Update assetUpdate = QueryBuilder.update(keyspace, table)
		assetUpdate.setConsistencyLevel(consistencyLevel)
		assetUpdate.with(QueryBuilder.set("status_flag", "Uploaded"))
		assetUpdate.where(QueryBuilder.eq("asset_id", objectId))
		
		cassandraOps.getCqlOperations()?.execute(assetUpdate)
	}
	
	
	@Override
	public def getDownloadDetails(UUID objectId, UploadAssetRequest uploadAssetRequest) {
		double defaultTimeout = 60
		def assetObject = getObject(objectId)
		def fileName = assetObject.fileName
		def timeOut = uploadAssetRequest.timeout? Double.parseDouble(uploadAssetRequest.timeout) : defaultTimeout
		def presignedUrl = awsS3Utils.generateUrlToGetObject(bucketName, fileName, timeOut)
		def results = [downloadUrl: presignedUrl]
		
		results
		
	}
	
	@Override
	public def searchObject(UploadAssetRequest uploadAssetRequest) {
	
		def fileName = uploadAssetRequest.fileName
		def solrQuery = "{\"q\":\"file_name:" + fileName + "\", \"start\":null,\"query.name\":\"searchObject\"}"
		Select selectQuery = QueryBuilder.select().from(keyspace, table) 
		selectQuery.where(QueryBuilder.eq("solr_query", solrQuery))
		
		logger.info("Select Query: ${selectQuery}")
		cassandraOps.select(selectQuery, UploadAsset.class)
		
	}
	
}
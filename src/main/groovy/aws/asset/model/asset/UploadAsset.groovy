package aws.asset.model.asset


import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table

@Table("assets")
class UploadAsset {	
	@PrimaryKey("asset_id") UUID assetId
	@Column("file_name") String fileName
	@Column("status_flag") String statusFlag
}
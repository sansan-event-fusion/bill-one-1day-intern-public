package sender.util

import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.ImpersonatedCredentials
import com.google.cloud.storage.*
import io.ktor.http.*
import sender.settings
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.nio.channels.Channels
import java.util.concurrent.TimeUnit

// fun createTenantBucketBlobId(tenantNameId: TenantNameId, name: String): BlobId {
//    return BlobId.of("tenant-${tenantNameId.value}.${settings.environment}.bill-one.com", "network-service/$name")
// }
//
// fun createMailInvoiceBucketBlobId(mailReceivedCallUUID: CallUUID, name: String): BlobId {
//    return BlobId.of(settings.mailInvoiceBucket, "${mailReceivedCallUUID.value}/$name")
// }

@Throws(IOException::class)
fun getStorageObject(blobId: BlobId): ByteArray {
    val storage = buildServiceClient()

    return storage.readAllBytes(blobId)
}

fun getStorageObjects(bucket: String, options: Storage.BlobListOption): List<Blob> {
    val storage = buildServiceClient()

    val blobs = storage.list(bucket, options)

    return blobs.iterateAll().toList()
}

@Throws(IOException::class)
fun getStorageInputStream(blobId: BlobId): InputStream {
    val storage = buildServiceClient()
    val reader = storage.reader(blobId)
    return Channels.newInputStream(reader)
}

fun copyStorageObject(sourceBlobId: BlobId, targetBlobId: BlobId) {
    val storage = buildServiceClient()

    val request = Storage.CopyRequest.newBuilder()
        .setSource(sourceBlobId)
        .setTarget(targetBlobId)
        .build()

    storage.copy(request)
}

fun deleteStorageObject(targetBlobId: BlobId) {
    val storage = buildServiceClient()

    storage.delete(targetBlobId)
}

fun createStorageObject(blobInfo: BlobInfo, content: ByteArray) {
    val storage = buildServiceClient()

    storage.create(blobInfo, content)
}

private fun buildServiceClient(): Storage {
    return StorageOptions.newBuilder().apply {
        if (settings.cloudStorageEmulatorHost != null) {
            setHost(settings.cloudStorageEmulatorHost)
        }
    }.build()
        .service
}

fun createStorageUrl(targetBlobInfo: BlobInfo): URL {
    if (settings.cloudStorageEmulatorHost != null) {
        return URL("${settings.cloudStorageEmulatorHost}/${targetBlobInfo.bucket}/${targetBlobInfo.name}")
    }
    val storage = buildServiceClient()
    val signer = ImpersonatedCredentials.create(
        GoogleCredentials.getApplicationDefault(),
        settings.serviceAccount,
        listOf(),
        listOf(),
        60
    )
    return storage.signUrl(
        targetBlobInfo,
        60,
        TimeUnit.SECONDS,
        Storage.SignUrlOption.withV4Signature(),
        Storage.SignUrlOption.signWith(signer)
    )
}

data class StorageObjectPath(
    val bucket: String,
    val path: String
) {
    companion object {
        fun fromBlobId(blobId: BlobId): StorageObjectPath {
            return StorageObjectPath(
                blobId.bucket,
                blobId.name
            )
        }
    }
}

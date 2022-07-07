package recipient.util

import com.google.api.core.ApiFunction
import com.google.api.gax.core.NoCredentialsProvider
import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider
import com.google.cloud.tasks.v2.CloudTasksSettings
import recipient.settings

fun buildCloudTasksSettings(): CloudTasksSettings = CloudTasksSettings.newBuilder().apply {
    if (settings.cloudTasksEmulatorHost != null) {
        // ローカル開発時（テストは除く）はエミュレーターを使う。
        // Cloud Pub/Subでエミュレーターを使うコード (https://cloud.google.com/pubsub/docs/emulator#accessing_environment_variables) を参考にしたけど、
        // ManagedChannelは一度shutdownすると使えなくなってしまうので、代わりにInstantiatingGrpcChannelProviderを使う。
        credentialsProvider = NoCredentialsProvider.create()
        transportChannelProvider = InstantiatingGrpcChannelProvider.newBuilder().apply {
            endpoint = settings.cloudTasksEmulatorHost
            channelConfigurator = ApiFunction { builder ->
                builder.usePlaintext()
            }
        }.build()
    }
}.build()

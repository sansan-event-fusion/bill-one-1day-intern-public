# 環境構築

## docker-compose で必要なアプリケーションを起動

以下のコマンドで react-app, Database(Postgres), cloud-tasks-emulator, cloud-storage-emulator を起動します。

```shell
$ docker-compose up --build -d
```

## マイグレーションとサンプルデータのインポート

```shell
$ ./operations/dbsetup.sh
```

## マイグレーションだけしたい場合

1day インターンでは使う予定はありませんが、拡張したい方向けにメモしておきます。

```shell
$ ./operations/migrate-all.sh
```

## Kotlin Application の実行

ホットリロードはないので、差分を反映したい場合は適時再起動

```shell
// kotlin-app-recipientの場合
$ cd /kotlin-app-recipient
$ ./gradlew run

// kotlin-app-senderの場合
$ cd /kotlin-app-recipient
$ ./gradlew run
```

## 動作確認

http://localhost:3000 にアクセスできれば OK です。

# 構成

## Frontend

FilePath: bill-one-1day-intern/react-app  
Port: 3000

## Backend

バックエンドは 2 つのマイクロサービスを使用する。それぞれで扱うデータを分ける。

### recipient

受領アカウント側のデータを扱うマイクロサービス  
FilePath: bill-one-1day-intern/kotlin-app-recipient  
Port: 8081

### sender

送付アカウント側のデータを扱うマイクロサービス  
FilePath: bill-one-1day-intern/kotlin-app-sender  
Port: 8082

## DB

Ports: 127.0.0.1:5432  
ID: postgres  
PW: なし

DATABASE:

- bill-one-1day-recipient
- bill-one-1day-recipient-test
- bill-one-1day-sender
- bill-one-1day-sender-test

### 関連ファイル

- 初期設定
  - Postgres 起動時に読み込まれる SQL
  - `/dockerfiles/postgres/initdb/recipient.sql`
  - `/dockerfiles/postgres/initdb/sender.sql`
- マイグレーションとサンプルデータのインポート
  - `/operations/dbsetup.sh`

## Cloud Tasks Emulator

Queuing サービスの Cloud Tasks をローカルでエミュレートする.  
Port: 9090

## Cloud Storage Emulator

Storage サービスの Cloud Storage をローカルでエミュレートする.  
Host: http://0.0.0.0:4443

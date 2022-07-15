# Kotlin サーバーサイド開発 1day Internship - Bill One

## 前提条件

### Docker Desktop

[公式ドキュメント](https://docs.docker.com/get-docker/)を参考に最新の Docker Desktop をインストールしてください。

### JDK11

#### macOS

[Homebrew](https://brew.sh/index_ja)を用いてインストールできます。
インストール時に出力されるメッセージを参考に PATH を通してください。

```shell
brew install openjdk@11
```

#### Windows

winget を用いてインストールできます。

参考: [Microsoft Build of OpenJDK をインストールする](https://docs.microsoft.com/ja-jp/java/openjdk/install#install-with-the-windows-package-manager)

```shell
winget install Microsoft.OpenJDK.11
```

### PostgreSQL

`psql` コマンドを用いてサンプルデータをインポートするため、PostgreSQL をインストールします。

#### macOS

```shell
brew install postgresql
```

#### Windows

```shell
winget install PostgreSQL.PostgreSQL
```

### Code Editor

以下のどちらかを想定していますが、アプリケーションの実行やテストは CLI 上で完結するため、お好きなものをご準備ください。

- IntelliJ IDEA Community Edition: [https://www.jetbrains.com/ja-jp/idea/](https://www.jetbrains.com/ja-jp/idea/)
- Visual Studio Code: [https://code.visualstudio.com/](https://code.visualstudio.com/)

## 環境構築

### リポジトリの Clone

任意のディレクトリにリポジトリを Clone します。

```shell
git clone https://github.com/eightcard/bill-one-1day-intern-public.git
```

### 各種コンテナを起動

以下のコマンドで react-app, Database(PostgreSQL), cloud-tasks-emulator, cloud-storage-emulator を起動します。
初回実行には少し時間がかかるため、インターン当日までに事前実行しておくことをオススメします。

```shell
docker compose up --build -d
```

### DB マイグレーションとサンプルデータのインポート

```shell
./operations/dbsetup.sh
```

**Database マイグレーションだけ実行したい場合**

1day インターンで使う予定はありませんが、拡張したい方向けにメモしておきます。

```shell
./operations/migrate-all.sh
```

### Kotlin Application の実行

※ ホットリロードを有効化していないため、差分を反映したい場合は適時再起動が必要です。

#### kotlin-app-recipient

```shell
cd /kotlin-app-recipient
./gradlew run
```

#### kotlin-app-sender

```shell
cd /kotlin-app-sender
./gradlew run
```

## 動作確認

http://localhost:3000 にアクセスできれば OK です。

## 構成

### Frontend

FilePath: [bill-one-1day-intern/react-app](./react-app)
Port: 3000

### Backend

バックエンドは 2 つのマイクロサービスで構成されています。
DB は各マイクロサービスごとに分離されています。

#### recipient

受領アカウント側のデータを扱うマイクロサービス
FilePath: [bill-one-1day-intern/kotlin-app-recipient](./kotlin-app-recipient)
Port: 8081

#### sender

送付アカウント側のデータを扱うマイクロサービス
FilePath: [bill-one-1day-intern/kotlin-app-sender](./kotlin-app-sender)
Port: 8082

### DB

PostgreSQL、マイクロサービスのデータストア
Port: 5432
ID: postgres
PW: なし

DATABASE:

- bill-one-1day-recipient
- bill-one-1day-recipient-test
- bill-one-1day-sender
- bill-one-1day-sender-test

#### DB 関連ファイル

- 初期設定
  - PostgreSQL 起動時に読み込まれる SQL
  - [./dockerfiles/postgres/initdb/recipient.sql](./dockerfiles/postgres/initdb/recipient.sql)
  - [./dockerfiles/postgres/initdb/sender.sql](./dockerfiles/postgres/initdb/sender.sql)
- DB マイグレーションとサンプルデータのインポート
  - [./operations/dbsetup.sh](./operations/dbsetup.sh)

### Cloud Tasks Emulator

Queuing サービスの Cloud Tasks をローカルでエミュレートする
Port: 9090

### Cloud Storage Emulator

Storage サービスの Cloud Storage をローカルでエミュレートする
Port: 4443

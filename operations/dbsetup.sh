#!/bin/bash

set -e

# リポジトリルートの絶対パス取得
REPOSITORY_ROOT=$(cd $(dirname $0)/../; pwd)

# 各データベースのマイグレーションと開発用データ作成
(cd "${REPOSITORY_ROOT}/kotlin-app-recipient" && ./operations/migrate.sh --local)
psql "host=localhost dbname=bill-one-1day-recipient user=postgres options=--search_path=recipient" -f "${REPOSITORY_ROOT}/kotlin-app-recipient/src/main/resources/db/sample_data.sql"

# 各データベースのマイグレーションと開発用データ作成
(cd "${REPOSITORY_ROOT}/kotlin-app-sender" && ./operations/migrate.sh --local)
psql "host=localhost dbname=bill-one-1day-sender user=postgres options=--search_path=sender" -f "${REPOSITORY_ROOT}/kotlin-app-sender/src/main/resources/db/sample_data.sql"
#!/bin/bash

set -e

# リポジトリルートの絶対パス取得
REPOSITORY_ROOT=$(cd $(dirname $0)/../; pwd)

# 各データベースのマイグレーションを実行:
(cd "${REPOSITORY_ROOT}/kotlin-app-recipient" && ./operations/migrate.sh --local)
(cd "${REPOSITORY_ROOT}/kotlin-app-sender" && ./operations/migrate.sh --local)


#!/bin/bash

# エラー時に停止する
set -e

# 第1引数のオプション解析
case $1 in
--local)
  # ローカル
  export FLYWAY_PASSWORD=
  CONFIG_FILE=src/main/resources/db/local.conf
  ;;
*)
  echo "Usage: $(basename $0) --local"
  exit 1
  ;;
esac

# 第2引数がある場合は終了
if [ -n "$2" ]; then
  echo "Usage: $(basename $0) --local"
  exit 1
fi

# ディレクトリにcdする
BASE_DIR=$(
  cd $(dirname $(dirname $0))
  pwd
)
cd $BASE_DIR

set -x
./gradlew flywayMigrate -Dflyway.configFiles=$CONFIG_FILE -i

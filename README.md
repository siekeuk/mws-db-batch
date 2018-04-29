# mws-db-batch
[mws-db-webapp](https://github.com/siekeuk/mws-db-webapp)で利用するカードデータのコンバートやサイトマップを作成するバッチです。

## Description
[mws-db-webapp](https://github.com/siekeuk/mws-db-webapp)の利用に必要な以下の機能を提供します。
- [Magic Set Editor](http://magicseteditor.sourceforge.net/)(以下MSE)から出力された[Magic Workstation](http://www.magicworkstation.com/)(以下MWS)形式のカードデータの変換とMongoDBへの投入
- 動的に生成されるページのサイトマップ作成

## Requirement
* JDK
* Maven
* MSEから出力されたMWS形式のカードデータ
* 上記の対訳ファイル  
  ファイル名を`{エキスパンションの略号}.lng`とし、以下の形式でテキストファイルを作成する必要があります。  
    ```
    {日本語カード名１}/{英語カード名１}
    {日本語カード名２}/{英語カード名２}
    {日本語カード名３}/{英語カード名３}
    ```

## Usage
### Settings
* `system.properties`
    * `db.*` - MongoDBの接続情報です。
    * `msePath` - MSEから出力したMWS形式のカードデータと対訳ファイルを配置するフォルダを設定します。
        ```
        {msePath}
          │   
          ├─ 20160101 <- 版情報のフォルダ名
          │   T16.lng <- 対訳ファイル
          │   T16.txt <- MWS形式のカードデータ
          │       
          ├─ 20170101
          │   T16.lng
          │   T16.txt
          │   T17.lng
          │   T17.txt
          │       
          └─ 20180101
              T16.lng
              T16.txt
              T17.lng
              T17.txt
              T18.lng
              T18.txt
        ```
    * `sitemapPath` - 作成するサイトマップの出力先を設定します。事前にフォルダを作成しておく必要があります。
    * `url` - サイトマップ作成に利用するサイトのURLです。

* `/src/define/Def.java`  
エキスパンションやカードタイプの定義を行っています。  
追加・変更がある場合、直接`Def.java`を編集し、Installを実施する必要があります。

### Install
```shell
mvn install
```
targetフォルダが作成され直下にjarファイルが2つ作成されます。
* `mws-db-batch-${project.version}.jar`  
依存性を含まないjarです。
* `mws-db-batch-${project.version}-jar-with-dependencies.jar`  
依存性を含むjarです。単一で実行可能なため、バッチ処理は基本こちらを使えば問題ないです。
### Run

※バッチ処理はいずれも既存データをドロップ、削除して再作成します。
必要であればバックアップを取るようにしてください。

* カードデータ投入
    ```sh
    java -cp target/mws-db-batch-1.0.0-jar-with-dependencies.jar batch.InsertCardInfo arg1 arg2...
    ```
    第一引数に`FULL`を指定すれば、指定フォルダの配下にあるカードデータすべて投入します。
    ```sh
    #すべて投入
    java -cp target/mws-db-batch-1.0.0-jar-with-dependencies.jar batch.InsertCardInfo FULL
    ```
    指定の版のみ投入したい場合は、版情報を指定してください。複数同時に処理可能です。
    ```sh
    #20160101と20180101の版のみ投入
    java -cp target/mws-db-batch-1.0.0-jar-with-dependencies.jar batch.InsertCardInfo 20160101 20180101
    ```

* サイトマップ作成
    ```sh
    #サイトマップ作成
    java -cp target/mws-db-batch-1.0.0-jar-with-dependencies.jar batch.CreateSiteMap
    ```
    `sitemapPath`で指定されたフォルダに制的コンテンツのサイトマップと動的コンテンツのサイトマップとそれらをまとめるサイトマップが生成されます。
    ```sh
    #出力例
    20160101.xml
    20170101.xml
    20180101.xml
    base.xml
    sitemap.xml
    ```

## Hints
生成後のjarを別のフォルダに移動して実行する場合は、実行時のカレントディレクトリに`system.properties`を配置してください。

## License
MIT
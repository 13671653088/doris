// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

import org.codehaus.groovy.runtime.IOGroovyMethods

suite("test_scalar_types_load", "p0") {

    def dataFile = """${getS3Url()}/regression/datatypes/test_scalar_types.csv"""

    // define dup key table
    def testTable = "tbl_scalar_types_dup"
    sql "DROP TABLE IF EXISTS ${testTable}"
    sql """
        CREATE TABLE IF NOT EXISTS ${testTable} (
            `k1` bigint(11) NULL,
            `c_bool` boolean NULL,
            `c_tinyint` tinyint(4) NULL,
            `c_smallint` smallint(6) NULL,
            `c_int` int(11) NULL,
            `c_bigint` bigint(20) NULL,
            `c_largeint` largeint(40) NULL,
            `c_float` float NULL,
            `c_double` double NULL,
            `c_decimal` decimal(20, 3) NULL,
            `c_decimalv3` decimalv3(20, 3) NULL,
            `c_date` date NULL,
            `c_datetime` datetime NULL,
            `c_datev2` datev2 NULL,
            `c_datetimev2` datetimev2(0) NULL,
            `c_char` char(15) NULL,
            `c_varchar` varchar(100) NULL,
            `c_string` text NULL
        ) ENGINE=OLAP
        DUPLICATE KEY(`k1`)
        COMMENT 'OLAP'
        DISTRIBUTED BY HASH(`k1`) BUCKETS 10
        PROPERTIES("replication_num" = "1");
        """

    // load data
    streamLoad {
        table testTable
        file dataFile
        time 60000

        check { result, exception, startTime, endTime ->
            if (exception != null) {
                throw exception
            }
            log.info("Stream load result: ${result}".toString())
            def json = parseJson(result)
            assertEquals(1000000, json.NumberTotalRows)
            assertEquals(1000000, json.NumberLoadedRows)
        }
    }


    // define unique key table1 enable mow
    testTable = "tbl_scalar_types_unique1"
    sql "DROP TABLE IF EXISTS ${testTable}"
    sql """
        CREATE TABLE IF NOT EXISTS ${testTable} (
            `c_datetimev2` datetimev2(0) NULL,
            `c_bigint` bigint(20) NULL,
            `c_decimalv3` decimalv3(20, 3) NULL,
            `c_bool` boolean NULL,
            `c_tinyint` tinyint(4) NULL,
            `c_smallint` smallint(6) NULL,
            `c_int` int(11) NULL,
            `c_largeint` largeint(40) NULL,
            `c_float` float NULL,
            `c_double` double NULL,
            `c_decimal` decimal(20, 3) NULL,
            `c_date` date NULL,
            `c_datetime` datetime NULL,
            `c_datev2` datev2 NULL,
            `c_char` char(15) NULL,
            `c_varchar` varchar(100) NULL,
            `c_string` text NULL
        ) ENGINE=OLAP
        UNIQUE KEY(`c_datetimev2`, `c_bigint`, `c_decimalv3`)
        COMMENT 'OLAP'
        DISTRIBUTED BY HASH(`c_bigint`) BUCKETS 10
        PROPERTIES("replication_num" = "1", "unique_key_merge_on_write" = "true");
        """
    
    // insert data into unique key table1 2 times
    sql """INSERT INTO ${testTable} SELECT `c_datetimev2`, `c_bigint`, `c_decimalv3`,
            `c_bool`, `c_tinyint`, `c_smallint`, `c_int`, `c_largeint`,
            `c_float`, `c_double`, `c_decimal`, `c_date`, `c_datetime`, `c_datev2`,
            `c_char`, `c_varchar`, `c_string` FROM tbl_scalar_types_dup"""
    sql """INSERT INTO ${testTable} SELECT `c_datetimev2`, `c_bigint`, `c_decimalv3`,
            `c_bool`, `c_tinyint`, `c_smallint`, `c_int`, `c_largeint`,
            `c_float`, `c_double`, `c_decimal`, `c_date`, `c_datetime`, `c_datev2`,
            `c_char`, `c_varchar`, `c_string` FROM tbl_scalar_types_dup"""


    // define unique key table2 disable mow
    testTable = "tbl_scalar_types_unique2"
    sql "DROP TABLE IF EXISTS ${testTable}"
    sql """
        CREATE TABLE IF NOT EXISTS ${testTable} (
            `c_datetimev2` datetimev2(0) NULL,
            `c_bigint` bigint(20) NULL,
            `c_decimalv3` decimalv3(20, 3) NULL,
            `c_bool` boolean NULL,
            `c_tinyint` tinyint(4) NULL,
            `c_smallint` smallint(6) NULL,
            `c_int` int(11) NULL,
            `c_largeint` largeint(40) NULL,
            `c_float` float NULL,
            `c_double` double NULL,
            `c_decimal` decimal(20, 3) NULL,
            `c_date` date NULL,
            `c_datetime` datetime NULL,
            `c_datev2` datev2 NULL,
            `c_char` char(15) NULL,
            `c_varchar` varchar(100) NULL,
            `c_string` text NULL
        ) ENGINE=OLAP
        UNIQUE KEY(`c_datetimev2`, `c_bigint`, `c_decimalv3`)
        COMMENT 'OLAP'
        DISTRIBUTED BY HASH(`c_bigint`) BUCKETS 10
        PROPERTIES("replication_num" = "1", "unique_key_merge_on_write" = "false");
        """
    
    // insert data into unique key table1 2 times
    sql """INSERT INTO ${testTable} SELECT `c_datetimev2`, `c_bigint`, `c_decimalv3`,
            `c_bool`, `c_tinyint`, `c_smallint`, `c_int`, `c_largeint`,
            `c_float`, `c_double`, `c_decimal`, `c_date`, `c_datetime`, `c_datev2`,
            `c_char`, `c_varchar`, `c_string` FROM tbl_scalar_types_dup"""
    sql """INSERT INTO ${testTable} SELECT `c_datetimev2`, `c_bigint`, `c_decimalv3`,
            `c_bool`, `c_tinyint`, `c_smallint`, `c_int`, `c_largeint`,
            `c_float`, `c_double`, `c_decimal`, `c_date`, `c_datetime`, `c_datev2`,
            `c_char`, `c_varchar`, `c_string` FROM tbl_scalar_types_dup"""

}

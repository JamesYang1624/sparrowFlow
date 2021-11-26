package com.xunai.common.http


/**
 * @author : yangweizheng
 * @date : 2021/5/12 17:12
 */
class ResponseException (var code: String, override var message: String) : RuntimeException()
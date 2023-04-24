package com.e.letsplant.data

class Post {
    var postId: String? = null
    var postImage: String? = null
    var description: String? = null
    var publisher: String? = null

    constructor()
    constructor(postId: String?, postImage: String?, description: String?, publisher: String?) {
        this.postId = postId
        this.postImage = postImage
        this.description = description
        this.publisher = publisher
    }
}
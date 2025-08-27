package org.wocy.registry

import org.wocy.visitor.BaseModelVisitor

class VisitorRegistry {

    private val visitors = hashMapOf<String, BaseModelVisitor>()

    constructor(visitors: List<Pair<String, BaseModelVisitor>>) {
        visitors.forEach {
            registry(it.first, it.second)
        }
    }

    fun registry(type: String, visitor: BaseModelVisitor) {
        visitors[type] = visitor
    }

    fun get(type: String): BaseModelVisitor =
        visitors[type] ?: throw RuntimeException("Visitor for `$type` not registered")
}

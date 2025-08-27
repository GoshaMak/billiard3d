package org.wocy.visitor

import org.wocy.model.BaseModelComposite
import org.wocy.model.BowlingBall
import org.wocy.model.Table

abstract class BaseModelVisitor {

    abstract fun visit(o: BowlingBall)

    abstract fun visit(o: Table)

    abstract fun visit(o: BaseModelComposite)
}

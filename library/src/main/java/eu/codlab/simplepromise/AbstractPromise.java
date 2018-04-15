package eu.codlab.simplepromise;

import eu.codlab.simplepromise.solve.PromiseExec;

/**
 * Created by kevinleperf on 15/04/2018.
 */

public abstract class AbstractPromise<TYPE_EXECUTE> {

    protected abstract <TYPE_RESULT> PromiseInOut<TYPE_EXECUTE, TYPE_RESULT>
    then(PromiseExec<TYPE_EXECUTE, TYPE_RESULT> to_resolve);
}

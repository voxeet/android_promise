package eu.codlab.simplepromise;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevinleperf on 15/04/2018.
 */

abstract class AbstractPromiseMulti<TYPE_EXECUTE> {


    private final ArrayList<AbstractPromise<TYPE_EXECUTE>> mPromises;

    private AbstractPromiseMulti() {
        mPromises = new ArrayList<>();
    }

    protected AbstractPromiseMulti(AbstractPromise<TYPE_EXECUTE> ...promises) {
        this();

        if (null != promises) {
            for (AbstractPromise<TYPE_EXECUTE> promise : promises) {
                mPromises.add(promise);
            }
        }
    }

    protected List<AbstractPromise<TYPE_EXECUTE>> getPromises() {
        return mPromises;
    }
}

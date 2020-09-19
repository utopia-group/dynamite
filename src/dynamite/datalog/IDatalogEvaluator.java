package dynamite.datalog;

import dynamite.datalog.ast.DatalogProgram;

/**
 * Interface for datalog evaluators.
 */
public interface IDatalogEvaluator {

    public DatalogOutput evaluate(DatalogProgram program);

}

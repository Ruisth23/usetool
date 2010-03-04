package org.tzi.use.uml.ocl.expr.operations;

import org.tzi.use.uml.ocl.expr.EvalContext;
import org.tzi.use.uml.ocl.expr.Expression;
import org.tzi.use.uml.ocl.type.Type;
import org.tzi.use.uml.ocl.value.Value;
import org.tzi.use.util.MultiMap;
import org.tzi.use.util.StringUtil;

/**
 * OpGeneric is the base class of a large group of individual operations. Each
 * operation is implemented by its own class deriving from OpGeneric. New
 * Operations can easily be added by writing a new operation class and adding a
 * single instance of the new class to the static list of operations in Class
 * ExpOperation (see below). Also, this way the new operation symbol is
 * immediately available to the specification compiler.
 * 
 * @version $ProjectVersion: 0.393 $
 * @author Mark Richters
 */

public abstract class OpGeneric {
    // These constants define different groups of operations. The
    // groups mainly differ wrt their behavior in case of undefined
    // arguments. The effects of passing any undefined argument to an
    // operation are as follows:
    //
    // OPERATION -> UndefinedValue(T) with T being the result type
    // of the operation
    // PREDICATE -> BooleanValue(false)
    // SPECIAL -> operation needs special treatment of undefined arguments
    public static final int OPERATION = 0;

    public static final int PREDICATE = 1;

    public static final int SPECIAL = 3;

    public abstract String name();

    public abstract int kind();

    public abstract boolean isInfixOrPrefix();

    public abstract Type matches(Type params[]);

    public abstract Value eval(EvalContext ctx, Value args[], Type resultType);

    public String stringRep(Expression args[], String atPre) {
        String res;
        if (isInfixOrPrefix()) {
            if (args.length == 1) {
                // e.g. `not true', -2, +3
                // insert blank between operator and expression to
                // avoid `--' which would be interpreted as comment
                res = name() + " " + args[0];
            } else
                // e.g. `3 + 4'
                res = "(" + StringUtil.fmtSeq(args, " " + name() + " ") + ")";
        } else {
            // translate into dot notation, e.g. foo->union(bla)
            res = name() + atPre;
            if (args.length > 0) {
                if (args[0].type().isCollection(true))
                    res = args[0] + "->" + res;
                else
                    res = args[0] + "." + res;
                if (args.length > 1)
                    res += "(" + StringUtil.fmtSeq(args, 1, ",") + ")";
            }
        }
        return res;
    }

	public static void registerOperations(MultiMap<String, OpGeneric> opmap) {
		// Basic operations
		StandardOperationsAny.registerTypeOperations(opmap);
		StandardOperationsObject.registerTypeOperations(opmap);
		
		// Basic types
		StandardOperationsNumber.registerTypeOperations(opmap);
		StandardOperationsString.registerTypeOperations(opmap);
		StandardOperationsBoolean.registerTypeOperations(opmap);
		
		// Collections
		StandardOperationsCollection.registerTypeOperations(opmap);
		StandardOperationsSet.registerTypeOperations(opmap);
		StandardOperationsBag.registerTypeOperations(opmap);
		StandardOperationsSequence.registerTypeOperations(opmap);
		StandardOperationsOrderedSet.registerTypeOperations(opmap);
		
		// Extensions
		StandardOperationsDate.registerTypeOperations(opmap);
	}
	
	public static void registerOperation(OpGeneric op, MultiMap<String, OpGeneric> opmap) {
		opmap.put(op.name(), op);
	}
}
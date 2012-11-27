package r.builtins;

import java.util.*;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.tools.*;
import r.nodes.truffle.*;

public class Primitives {

    private static Map<RSymbol, PrimitiveEntry> map;
    static {
        map = new HashMap<>();
        add(":", 2, 2, Colon.FACTORY);
        add("+", 1, 2, Operators.ADD_FACTORY);
        add("-", 1, 2, Operators.SUB_FACTORY);
        add("*", 2, 2, Operators.MULT_FACTORY);
        add("/", 2, 2, Operators.DIV_FACTORY);
        add("%*%", 2, 2, Operators.MAT_MULT_FACTORY);
        add("%o%", 2, 2, Operators.OUTER_MULT_FACTORY);
        add("as.character", 0, -1, Cast.STRING_FACTORY);
        add("as.double", 0, -1, Cast.DOUBLE_FACTORY);
        add("as.integer", 0, -1, Cast.INT_FACTORY);
        add("as.vector", 1, 2, Cast.VECTOR_FACTORY);
        add("c", 0, -1, Combine.FACTORY);
        add("cat", 0, -1, Cat.FACTORY);
        add("colMeans", 1, 3, ColumnsRowsStats.COLMEANS_FACTORY);
        add("colSums", 1, 3, ColumnsRowsStats.COLSUMS_FACTORY);
        add("cumsum", 1, 1, CumulativeSum.FACTORY);
        add("diag<-", 2, 2, Diagonal.REPLACEMENT_FACTORY);
        add("dim", 1, 1, Dimensions.DIM_FACTORY);
        add("double", 0, 1, ArrayConstructor.DOUBLE_FACTORY);
        add("integer", 0, 1, ArrayConstructor.INT_FACTORY);
        add("is.na", 1, 1, IsNA.FACTORY);
        add("lapply", 2, -1, Apply.LAPPLY_FACTORY);
        add("length", 1, 1, Length.FACTORY);
        add("list", 0, -1, List.FACTORY);
        add("logical", 0, 1, ArrayConstructor.LOGICAL_FACTORY);
        add("lower.tri", 1, 2, TriangularPart.LOWER_FACTORY);
        add("matrix", 0, 5, Matrix.FACTORY);
        add("max", 0, -1, Extreme.MAX_FACTORY);
        add("min", 0, -1, Extreme.MIN_FACTORY);
        add("nchar", 1, 3, NChar.FACTORY);
        add("ncol", 1, 1, Dimensions.NCOL_FACTORY);
        add("nrow", 1, 1, Dimensions.NROW_FACTORY);
        add("outer", 2, -1, Outer.FACTORY);
        add("rep", 2, 2, Rep.FACTORY); // in fact rep.int
        add("rep.int", 2, 2, Rep.FACTORY);
        add("return", 0, 1, Return.FACTORY);
        add("rowMeans", 1, 3, ColumnsRowsStats.ROWMEANS_FACTORY);
        add("rowSums", 1, 3, ColumnsRowsStats.ROWSUMS_FACTORY);
        add("sapply", 2, -1, Apply.SAPPLY_FACTORY);
        add("seq", 0, -1, Seq.FACTORY);  // in fact seq.default (and only part of it)
        add("seq.default", 0, -1, Seq.FACTORY);
        add("strsplit", 1, 5, StrSplit.FACTORY);
        add("sum", 0, -1, Sum.FACTORY);
        add("sqrt", 1, 1, Sqrt.FACTORY);
        add("upper.tri", 1, 2, TriangularPart.UPPER_FACTORY);
        add("which", 1, 3, Which.FACTORY);
    }

    public static CallFactory getCallFactory(final RSymbol name, final RFunction enclosing) {
        final PrimitiveEntry pe = Primitives.get(name, enclosing);
        if (pe == null) {
            return null;
        }
        return new CallFactory() {

            @Override
            public RSymbol name() {
                return name;
            }

            @Override
            public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
                int minArgs = pe.getMinArgs();
                int maxArgs = pe.getMaxArgs();

                if (minArgs != -1 && exprs.length < minArgs || maxArgs != -1 && exprs.length > maxArgs) {
                    throw RError.getGenericError(call, "Wrong number of arguments for call to BuiltIn (" + PrettyPrinter.prettyPrint(call) + ")");
                }

                return pe.factory.create(call, names, exprs);
            }
        };
    }

    public static PrimitiveEntry get(RSymbol name, RFunction fun) {
        PrimitiveEntry pe = get(name);
        if (pe != null && fun != null && fun.isInWriteSet(name)) {
            Utils.nyi(); // TODO case when a primitive is shadowed by a local symbol
        }                // FIXME: but shouldn't we keep traversing recursively through all frames of the caller?
                         // FIXME: also, what about reflections?
        return pe;
    }

    public static PrimitiveEntry get(RSymbol name) {
        return map.get(name);
    }

    private static void add(String name, int minArgs, int maxArgs, CallFactory body) {
        add(name, minArgs, maxArgs, body, PrimitiveEntry.PREFIX);
    }

    private static void add(String name, int minArgs, int maxArgs, CallFactory body, int pp) {
        RSymbol sym = RSymbol.getSymbol(name);
        assert Utils.check(!map.containsKey(sym));
        map.put(sym, new PrimitiveEntry(sym, minArgs, maxArgs, body, pp));
    }
}

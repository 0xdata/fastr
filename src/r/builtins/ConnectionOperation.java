package r.builtins;

import com.oracle.truffle.api.frame.*;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;


public class ConnectionOperation {

    public static final CallFactory CLOSE_FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {

            BuiltIn.ensureArgName(call, "con", names[0]);

            return new BuiltIn.BuiltIn1(call, names, exprs) {

                @Override
                public final RAny doBuiltIn(Frame frame, RAny arg) {
                    if (arg instanceof RInt) {
                        RInt iarg = (RInt) arg;
                        if (iarg.size() == 1) {
                            int cindex = iarg.getInt(0);
                            Connection con = RContext.getConnection(cindex);
                            if (con != null) {
                                if (con.isOpen()) {
                                    con.close(ast);
                                }
                                RContext.freeConnection(cindex);
                                return RNull.getNull();
                            } else {
                                throw RError.getInvalidConnection(ast);
                            }
                        }
                    }
                    Utils.nyi("unsupported argument");
                    return null;
                }
            };
        }
    };

    public static final CallFactory FLUSH_FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {

            BuiltIn.ensureArgName(call, "con", names[0]);

            return new BuiltIn.BuiltIn1(call, names, exprs) {

                @Override
                public final RAny doBuiltIn(Frame frame, RAny arg) {
                    if (arg instanceof RInt) {
                        RInt iarg = (RInt) arg;
                        if (iarg.size() == 1) {
                            int cindex = iarg.getInt(0);
                            Connection con = RContext.getConnection(cindex);
                            if (con != null) {
                                con.flush(ast);
                                return RNull.getNull();
                            } else {
                                throw RError.getInvalidConnection(ast);
                            }
                        }
                    }
                    Utils.nyi("unsupported argument");
                    return null;
                }
            };
        }
    };


}

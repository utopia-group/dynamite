package dynamite.datalog.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dynamite.datalog.ast.BinaryPredicate;
import dynamite.datalog.ast.BinaryPredicate.Operator;
import dynamite.datalog.ast.ConcatFunction;
import dynamite.datalog.ast.DatalogExpression;
import dynamite.datalog.ast.DatalogFact;
import dynamite.datalog.ast.DatalogPredicate;
import dynamite.datalog.ast.DatalogProgram;
import dynamite.datalog.ast.DatalogRule;
import dynamite.datalog.ast.DatalogStatement;
import dynamite.datalog.ast.Identifier;
import dynamite.datalog.ast.InputDeclaration;
import dynamite.datalog.ast.IntLiteral;
import dynamite.datalog.ast.OutputDeclaration;
import dynamite.datalog.ast.RelationDeclaration;
import dynamite.datalog.ast.RelationPredicate;
import dynamite.datalog.ast.StringLiteral;
import dynamite.datalog.ast.TypeDeclaration;
import dynamite.datalog.ast.TypedAttribute;
import dynamite.datalog.parser.DatalogParser.AttrContext;
import dynamite.datalog.parser.DatalogParser.AttrsContext;
import dynamite.datalog.parser.DatalogParser.BiPredContext;
import dynamite.datalog.parser.DatalogParser.BodiesContext;
import dynamite.datalog.parser.DatalogParser.CatContext;
import dynamite.datalog.parser.DatalogParser.DatalogPredContext;
import dynamite.datalog.parser.DatalogParser.DatalogRuleContext;
import dynamite.datalog.parser.DatalogParser.DeclContext;
import dynamite.datalog.parser.DatalogParser.DeclsContext;
import dynamite.datalog.parser.DatalogParser.EntryContext;
import dynamite.datalog.parser.DatalogParser.ExpContext;
import dynamite.datalog.parser.DatalogParser.ExpListContext;
import dynamite.datalog.parser.DatalogParser.FactContext;
import dynamite.datalog.parser.DatalogParser.FactsContext;
import dynamite.datalog.parser.DatalogParser.HeadsContext;
import dynamite.datalog.parser.DatalogParser.InputDeclContext;
import dynamite.datalog.parser.DatalogParser.OpContext;
import dynamite.datalog.parser.DatalogParser.OutputDeclContext;
import dynamite.datalog.parser.DatalogParser.ParamMapContext;
import dynamite.datalog.parser.DatalogParser.ProgramContext;
import dynamite.datalog.parser.DatalogParser.RelPredContext;
import dynamite.datalog.parser.DatalogParser.RelationDeclContext;
import dynamite.datalog.parser.DatalogParser.RulesContext;
import dynamite.datalog.parser.DatalogParser.TypeDeclContext;

public class AntlrToAstVisitor extends DatalogBaseVisitor<Object> {

    @Override
    public DatalogProgram visitProgram(ProgramContext context) {
        List<DatalogStatement> decls = visitDecls(context.decls());
        List<DatalogRule> rules = visitRules(context.rules());
        List<DatalogFact> facts = visitFacts(context.facts());
        return new DatalogProgram(decls, rules, facts);
    }

    @Override
    public List<DatalogStatement> visitDecls(DeclsContext ctx) {
        List<DatalogStatement> decls = new ArrayList<>();
        for (DeclContext declCtx : ctx.decl()) {
            decls.add(visitDecl(declCtx));
        }
        return decls;
    }

    @Override
    public DatalogStatement visitDecl(DeclContext ctx) {
        if (ctx.inputDecl() != null) {
            return visitInputDecl(ctx.inputDecl());
        } else if (ctx.outputDecl() != null) {
            return visitOutputDecl(ctx.outputDecl());
        } else if (ctx.relationDecl() != null) {
            return visitRelationDecl(ctx.relationDecl());
        } else if (ctx.typeDecl() != null) {
            return visitTypeDecl(ctx.typeDecl());
        }
        assert false : "AntlrToAst visitDecl context not match";
        return null;
    }

    @Override
    public InputDeclaration visitInputDecl(InputDeclContext ctx) {
        String relation = ctx.ID().toString();
        return ctx.paramMap() == null
                ? new InputDeclaration(relation)
                : new InputDeclaration(relation, visitParamMap(ctx.paramMap()));
    }

    @Override
    public OutputDeclaration visitOutputDecl(OutputDeclContext ctx) {
        String relation = ctx.ID().toString();
        return ctx.paramMap() == null
                ? new OutputDeclaration(relation)
                : new OutputDeclaration(relation, visitParamMap(ctx.paramMap()));
    }

    @Override
    public Map<String, String> visitParamMap(ParamMapContext ctx) {
        List<List<String>> mapEntries = new ArrayList<>();
        for (EntryContext entryCtx : ctx.entry()) {
            mapEntries.add(visitEntry(entryCtx));
        }
        Map<String, String> res = new HashMap<>();
        for (List<String> mapEntry : mapEntries) {
            res.put(mapEntry.get(0), mapEntry.get(1));
        }
        return res;
    }

    @Override
    public List<String> visitEntry(EntryContext ctx) {
        List<String> res = new ArrayList<>();
        res.add(ctx.ID(0).toString());
        if (ctx.ID(1) != null) {
            res.add(ctx.ID(1).toString());
        } else if (ctx.STRING() != null) {
            res.add(ctx.STRING().toString());
        } else {
            assert false : "AntlrToAst visitEntry context not match";
        }
        return res;
    }

    @Override
    public RelationDeclaration visitRelationDecl(RelationDeclContext ctx) {
        String relation = ctx.ID().toString();
        List<TypedAttribute> attributes = visitAttrs(ctx.attrs());
        return new RelationDeclaration(relation, attributes);
    }

    @Override
    public List<TypedAttribute> visitAttrs(AttrsContext ctx) {
        List<TypedAttribute> res = new ArrayList<>();
        for (AttrContext attrCtx : ctx.attr()) {
            res.add(visitAttr(attrCtx));
        }
        return res;
    }

    @Override
    public TypedAttribute visitAttr(AttrContext ctx) {
        String attrName = ctx.ID(0).toString();
        String typeName = ctx.ID(1).toString();
        return new TypedAttribute(attrName, typeName);
    }

    @Override
    public TypeDeclaration visitTypeDecl(TypeDeclContext ctx) {
        String typeName = ctx.ID().toString();
        return new TypeDeclaration(typeName);
    }

    @Override
    public List<DatalogRule> visitRules(RulesContext ctx) {
        List<DatalogRule> res = new ArrayList<>();
        for (DatalogRuleContext ruleCtx : ctx.datalogRule()) {
            res.add(visitDatalogRule(ruleCtx));
        }
        return res;
    }

    @Override
    public DatalogRule visitDatalogRule(DatalogRuleContext ctx) {
        List<RelationPredicate> heads = visitHeads(ctx.heads());
        List<DatalogPredicate> bodies = visitBodies(ctx.bodies());
        return new DatalogRule(heads, bodies);
    }

    @Override
    public List<RelationPredicate> visitHeads(HeadsContext ctx) {
        List<RelationPredicate> res = new ArrayList<>();
        for (RelPredContext relCtx : ctx.relPred()) {
            res.add(visitRelPred(relCtx));
        }
        return res;
    }

    @Override
    public RelationPredicate visitRelPred(RelPredContext ctx) {
        String relation = ctx.ID().toString();
        List<DatalogExpression> arguments = visitExpList(ctx.expList());
        return new RelationPredicate(relation, arguments);
    }

    @Override
    public List<DatalogExpression> visitExpList(ExpListContext ctx) {
        List<DatalogExpression> res = new ArrayList<>();
        for (ExpContext expCtx : ctx.exp()) {
            res.add(visitExp(expCtx));
        }
        return res;
    }

    @Override
    public DatalogExpression visitExp(ExpContext ctx) {
        if (ctx.cat() != null) {
            return visitCat(ctx.cat());
        } else if (ctx.ID() != null) {
            return new Identifier(ctx.ID().toString());
        } else if (ctx.INT() != null) {
            return new IntLiteral(Integer.valueOf(ctx.INT().toString()));
        } else if (ctx.STRING() != null) {
            String stringWithQuotes = ctx.STRING().toString();
            return new StringLiteral(stringWithQuotes.substring(1, stringWithQuotes.length() - 1));
        }
        assert false : "AntlrToAst visitExp context not match";
        return null;
    }

    @Override
    public ConcatFunction visitCat(CatContext ctx) {
        DatalogExpression lhs = visitExp(ctx.exp(0));
        DatalogExpression rhs = visitExp(ctx.exp(1));
        return new ConcatFunction(lhs, rhs);
    }

    @Override
    public List<DatalogPredicate> visitBodies(BodiesContext ctx) {
        List<DatalogPredicate> res = new ArrayList<>();
        for (DatalogPredContext predCtx : ctx.datalogPred()) {
            res.add(visitDatalogPred(predCtx));
        }
        return res;
    }

    @Override
    public DatalogPredicate visitDatalogPred(DatalogPredContext ctx) {
        if (ctx.biPred() != null) {
            return visitBiPred(ctx.biPred());
        } else if (ctx.relPred() != null) {
            return visitRelPred(ctx.relPred());
        }
        assert false : "AntlrToAst visitDatalogPred context not match";
        return null;
    }

    @Override
    public BinaryPredicate visitBiPred(BiPredContext ctx) {
        DatalogExpression lhs = visitExp(ctx.exp(0));
        Operator op = visitOp(ctx.op());
        DatalogExpression rhs = visitExp(ctx.exp(1));
        return new BinaryPredicate(lhs, op, rhs);
    }

    @Override
    public Operator visitOp(OpContext ctx) {
        if (ctx.EQ() != null) {
            return BinaryPredicate.stringToOperator(ctx.EQ().toString());
        } else if (ctx.NE() != null) {
            return BinaryPredicate.stringToOperator(ctx.NE().toString());
        } else if (ctx.LT() != null) {
            return BinaryPredicate.stringToOperator(ctx.LT().toString());
        } else if (ctx.LE() != null) {
            return BinaryPredicate.stringToOperator(ctx.LE().toString());
        } else if (ctx.GT() != null) {
            return BinaryPredicate.stringToOperator(ctx.GT().toString());
        } else if (ctx.GE() != null) {
            return BinaryPredicate.stringToOperator(ctx.GE().toString());
        }
        assert false : "AntlrToAst visitOp context not match";
        return null;
    }

    @Override
    public List<DatalogFact> visitFacts(FactsContext ctx) {
        List<DatalogFact> res = new ArrayList<>();
        for (FactContext factCtx : ctx.fact()) {
            res.add(visitFact(factCtx));
        }
        return res;
    }

    @Override
    public DatalogFact visitFact(FactContext ctx) {
        RelationPredicate relationPred = visitRelPred(ctx.relPred());
        return new DatalogFact(relationPred);
    }

}

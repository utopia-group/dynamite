package dynamite.datalog;

import org.junit.Assert;
import org.junit.Test;

import dynamite.datalog.ast.DatalogProgram;

public class UniverseGenVisitorTest {

    @Test
    public void test1() {
        DatalogProgram program = DatalogTestUtils.buildDatalogProgram1();
        DatalogProgram progWithUniv = program.accept(new UniverseGenVisitor());
        String expected = ""
                + ".decl R(a: symbol, b: symbol)\n"
                + ".output R(delimiter=\"\\t\")\n"
                + ".decl __Universe(_R?a: symbol, _R?b: symbol)\n"
                + ".output __Universe(delimiter=\"\\t\")\n"
                + "\n"
                + "R(x, y) :-\n"
                + "    R(y, x).\n"
                + "__Universe(v_R?a, v_R?b) :-\n"
                + "    R(v_R?a, v_R?b).\n"
                + "\n"
                + "R(\"1\", \"1\").\n"
                + "R(\"1\", \"2\").\n"
                + "R(\"1\", \"3\").\n";
        Assert.assertEquals(expected, progWithUniv.toSouffle());
    }

    @Test
    public void test2() {
        DatalogProgram program = DatalogTestUtils.buildDatalogProgram2();
        DatalogProgram progWithUniv = program.accept(new UniverseGenVisitor());
        String expected = ""
                + ".decl A?B(a: symbol, b: symbol)\n"
                + ".output A?B(delimiter=\"\\t\")\n"
                + ".decl __Universe(_A?B?a: symbol, _A?B?b: symbol)\n"
                + ".output __Universe(delimiter=\"\\t\")\n"
                + "\n"
                + "A?B(x, z) :-\n"
                + "    A?B(x, y),\n"
                + "    A?B(y, z).\n"
                + "__Universe(v_A?B?a, v_A?B?b) :-\n"
                + "    A?B(v_A?B?a, v_A?B?b).\n"
                + "\n"
                + "A?B(\"1\", \"2\").\n"
                + "A?B(\"2\", \"3\").\n";
        Assert.assertEquals(expected, progWithUniv.toSouffle());
    }

    @Test
    public void test3() {
        DatalogProgram program = DatalogTestUtils.buildDatalogProgram3();
        DatalogProgram progWithUniv = program.accept(new UniverseGenVisitor());
        String expected = ""
                + ".type IntAttr\n"
                + ".type StrAttr\n"
                + ".type Rel\n"
                + ".decl C(c1: StrAttr, c2: StrAttr, c3: StrAttr, c4: StrAttr)\n"
                + ".decl A?B(__id: Rel, b1: StrAttr, b2: StrAttr)\n"
                + ".decl A(a1: StrAttr, a2: StrAttr, B: Rel)\n"
                + ".output A?B(delimiter=\"\\t\")\n"
                + ".output A(delimiter=\"\\t\")\n"
                + ".decl __Universe(_A?a1: StrAttr, _A?a2: StrAttr, _A?B: Rel, _A?B?__id: Rel, _A?B?b1: StrAttr, _A?B?b2: StrAttr)\n"
                + ".output __Universe(delimiter=\"\\t\")\n"
                + "\n"
                + "A?B(_v_0, v_A?B?b1, v_A?B?b2), A(v_A?a1, v_A?a2, _v_0) :-\n"
                + "    C(v_A?a1, v_A?a2, v_A?B?b1, v_A?B?b2),\n"
                + "    _v_0 = cat(\"#\", cat(v_A?a1, cat(\"#\", cat(v_A?a2, \"#\")))).\n"
                + "__Universe(v_A?a1, v_A?a2, v_A?B, v_A?B, v_A?B?b1, v_A?B?b2) :-\n"
                + "    A(v_A?a1, v_A?a2, v_A?B),\n"
                + "    A?B(v_A?B, v_A?B?b1, v_A?B?b2).\n"
                + "\n"
                + "C(\"1\", \"2\", \"3\", \"4\").\n";
        Assert.assertEquals(expected, progWithUniv.toSouffle());
    }

}

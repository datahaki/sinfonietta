package snf.par;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.sonata.enc.ly.PagesParam;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.Tolerance;

class PagesParamTest {
  @Test
  void test1() {
    PagesParam pagesParam = new PagesParam();
    pagesParam.measures = Tensors.vector(0, 10, 30);
    // PlayalongParam pagesParam = new PlayalongParam(1);
    pagesParam.pageTurn = RationalScalar.of(1, 1);
    assertEquals(pagesParam.pageOf(RealScalar.of(-1)), RealScalar.ZERO);
    assertEquals(pagesParam.pageOf(RealScalar.of(0)), RealScalar.ZERO);
    assertEquals(pagesParam.pageOf(RealScalar.of(3)), RealScalar.ZERO);
    assertEquals(pagesParam.pageOf(RealScalar.of(9)), RealScalar.ZERO);
    Tolerance.CHOP.requireClose(pagesParam.pageOf(RealScalar.of(9.2)), RealScalar.of(0.2));
    Tolerance.CHOP.requireClose(pagesParam.pageOf(RealScalar.of(9.75)), RealScalar.of(0.75));
    assertEquals(pagesParam.pageOf(RealScalar.of(10)), RealScalar.ONE);
    assertEquals(pagesParam.pageOf(RealScalar.of(20)), RealScalar.ONE);
    Tolerance.CHOP.requireClose(pagesParam.pageOf(RealScalar.of(29.2)), RealScalar.of(1.2));
    Tolerance.CHOP.requireClose(pagesParam.pageOf(RealScalar.of(29.75)), RealScalar.of(1.75));
    assertEquals(pagesParam.pageOf(RealScalar.of(12330.3)), RealScalar.of(2));
  }

  @Test
  void test2() {
    PagesParam pagesParam = new PagesParam();
    pagesParam.measures = Tensors.vector(0, 10, 30);
    // PlayalongParam pagesParam = new PlayalongParam(1);
    pagesParam.pageTurn = RationalScalar.of(1, 2);
    assertEquals(pagesParam.pageOf(RealScalar.of(-1)), RealScalar.ZERO);
    assertEquals(pagesParam.pageOf(RealScalar.of(0)), RealScalar.ZERO);
    assertEquals(pagesParam.pageOf(RealScalar.of(3)), RealScalar.ZERO);
    assertEquals(pagesParam.pageOf(RealScalar.of(9)), RealScalar.ZERO);
    Tolerance.CHOP.requireClose(pagesParam.pageOf(RealScalar.of(9.2)), RealScalar.of(0.0));
    Tolerance.CHOP.requireClose(pagesParam.pageOf(RealScalar.of(9.75)), RealScalar.of(0.5));
    assertEquals(pagesParam.pageOf(RealScalar.of(10)), RealScalar.ONE);
    assertEquals(pagesParam.pageOf(RealScalar.of(20)), RealScalar.ONE);
    Tolerance.CHOP.requireClose(pagesParam.pageOf(RealScalar.of(29.2)), RealScalar.of(1.0));
    Tolerance.CHOP.requireClose(pagesParam.pageOf(RealScalar.of(29.75)), RealScalar.of(1.5));
    assertEquals(pagesParam.pageOf(RealScalar.of(12330.3)), RealScalar.of(2));
  }
}

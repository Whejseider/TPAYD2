package view.component.chart.renderer.bar;

import org.jfree.chart.renderer.category.BarRenderer;
import view.component.chart.themes.ChartDrawingSupplier;


public class ChartBarRenderer extends BarRenderer {

    public ChartBarRenderer() {
        initStyle();
    }

    private void initStyle() {
        setDefaultLegendShape(ChartDrawingSupplier.getDefaultShape());
    }

    @Override
    public String toString() {
        return "Bar";
    }
}

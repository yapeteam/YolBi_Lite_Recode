package org.cef;

import java.awt.Component;
import java.awt.Point;

public class DummyComponent extends Component {
    public Point getLocationOnScreen() {
        return new Point(0, 0);
    }
}

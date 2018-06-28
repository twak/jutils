package org.twak.utils.ui.auto;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention( RetentionPolicy.RUNTIME )

public @interface AutoRange {
	
	double min();
	double max();
	double step();
}

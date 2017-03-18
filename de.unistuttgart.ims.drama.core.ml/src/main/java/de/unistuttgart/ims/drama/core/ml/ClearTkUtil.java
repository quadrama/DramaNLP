package de.unistuttgart.ims.drama.core.ml;

import java.util.Formatter;
import java.util.Locale;

import org.cleartk.eval.util.ConfusionMatrix;

public class ClearTkUtil {
	public static <T extends Comparable<? super T>> String toCmdLine(ConfusionMatrix<T> cm) {
		StringBuilder builder = new StringBuilder();
		Formatter formatter = new Formatter(builder, Locale.US);
		// Header Row
		builder.append("Predicted Class →\n");
		builder.append("↓ Actual Class\n");

		// Predicted Classes Header Row
		formatter.format("%5s ", " ");

		for (T predicted : cm.getClasses()) {
			formatter.format("%5s ", predicted);
		}
		formatter.format("%5s\n", "Total");

		// Data Rows
		// String firstColumnLabel = "Actual Class,";
		for (T actual : cm.getClasses()) {
			// builder.append(firstColumnLabel);
			// firstColumnLabel = ",";
			builder.append(String.format("%5s ", actual));

			for (T predicted : cm.getClasses()) {
				formatter.format("%5d ", cm.getCount(actual, predicted));
			}
			// Actual Class Totals Column
			formatter.format("%5d\n", cm.getActualTotal(actual));
		}

		// Predicted Class Totals Row
		formatter.format("%5s ", "Total");
		for (T predicted : cm.getClasses()) {
			formatter.format("%5d ", cm.getPredictedTotal(predicted));
		}
		builder.append("\n");

		formatter.flush();
		formatter.close();
		return builder.toString();
	}
}

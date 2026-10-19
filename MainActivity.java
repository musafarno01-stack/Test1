package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView display;
    private String currentInput = "";
    private String storedInput = null;
    private String operator = null;
    private boolean justEvaluated = false;

    private final DecimalFormat numberFormat = new DecimalFormat("#.##########", new DecimalFormatSymbols(Locale.US));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.display);
        updateDisplay();
    }

    public void onDigitClick(View view) {
        String digit = (String) view.getTag();
        if (digit == null) return;

        if (justEvaluated) {
            currentInput = "";
            justEvaluated = false;
        }

        if (digit.equals(".")) {
            if (currentInput.contains(".")) return;
            if (currentInput.isEmpty()) currentInput = "0";
            currentInput += ".";
        } else {
            if (currentInput.equals("0")) currentInput = "";
            currentInput += digit;
        }
        updateDisplay();
    }

    public void onOperatorClick(View view) {
        String op = (String) view.getTag();
        if (op == null) return;

        if (!currentInput.isEmpty()) {
            if (storedInput != null && operator != null && !justEvaluated) {
                compute();
            }
            if (storedInput == null || justEvaluated) {
                storedInput = currentInput;
            }
            currentInput = "";
        } else if (storedInput == null) {
            storedInput = "0";
        }
        operator = op;
        justEvaluated = false;
        updatePreview();
    }

    public void onEqualsClick(View view) {
        if (storedInput != null && operator != null) {
            compute();
            operator = null;
            storedInput = null;
            justEvaluated = true;
        }
    }

    private void compute() {
        if (storedInput == null || operator == null || currentInput.isEmpty()) {
            return;
        }
        double a = parseSafe(storedInput);
        double b = parseSafe(currentInput);
        double result = 0;

        switch (operator) {
            case "+": result = a + b; break;
            case "-": result = a - b; break;
            case "*": result = a * b; break;
            case "/":
                if (b == 0) {
                    currentInput = "Error";
                    display.setText("Error");
                    return;
                }
                result = a / b;
                break;
            default: return;
        }

        currentInput = formatNumber(result);
        storedInput = currentInput;
        updateDisplay();
    }

    private double parseSafe(String value) {
        if (value == null || value.isEmpty()) return 0;
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String formatNumber(double value) {
        if (Double.isInfinite(value) || Double.isNaN(value)) return "Error";
        String formatted = numberFormat.format(value);
        if (formatted.contains(".")) {
            formatted = formatted.replaceAll("0+$", "");
            formatted = formatted.replaceAll("\\.$", "");
        }
        return formatted.isEmpty() ? "0" : formatted;
    }

    public void onClearClick(View view) {
        currentInput = "";
        storedInput = null;
        operator = null;
        justEvaluated = false;
        updateDisplay();
    }

    public void onDeleteClick(View view) {
        if (justEvaluated || currentInput.equals("Error")) {
            currentInput = "";
            justEvaluated = false;
        } else if (currentInput.length() > 0) {
            currentInput = currentInput.substring(0, currentInput.length() - 1);
        }
        updateDisplay();
    }

    private void updateDisplay() {
        String text;
        if (currentInput.isEmpty()) {
            text = "0";
        } else {
            text = currentInput;
        }
        display.setText(text);
    }

    private void updatePreview() {
        if (storedInput != null && operator != null) {
            display.setText(storedInput + " " + operator);
        } else {
            updateDisplay();
        }
    }
}

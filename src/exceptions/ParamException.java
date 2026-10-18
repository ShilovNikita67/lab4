package exceptions;

public class ParamException extends IllegalArgumentException {
    private static final long serialVersionUID = 1L;

    private final String parameter;

    public ParamException(String parameter, String reason) {
        super("Параметр '" + parameter + "': " + reason);
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }

    public static double requirePositiveFinite(String parameter, double value) {
        if (Double.isNaN(value)) {
            throw new ParamException(parameter, "значение не является числом (NaN)");
        }
        if (Double.isInfinite(value)) {
            throw new ParamException(parameter, "значение должно быть конечным, получено " + value);
        }
        if (value <= 0) {
            throw new ParamException(parameter, "значение должно быть положительным, получено " + value);
        }
        return value;
    }
}

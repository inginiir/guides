package com.kalita.service;

import com.kalita.model.Attribute;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class AttributeService {

    private static final Logger logger = Logger.getLogger(AttributeService.class.getName());

    public boolean sendQueryEnum(Attribute attribute) {
        String statement = Operator.parseOperator(attribute.operator())
                .buildRequest(attribute.name(), attribute.value());
        logger.log(Level.INFO, () -> "Sending query to external service... " + statement);
        return true;
    }

    public boolean sendQuerySwitch(Attribute attribute) {
        String statement = switch (attribute.operator()) {
            case "EQUALS" -> String.format("%s %s %s", attribute.name(), "==", attribute.value());
            case "NOT_EQUALS" -> String.format("%s %s %s", attribute.name(), "!=", attribute.value());
            case "GREATER_EQUALS" -> String.format("%s %s %s", attribute.name(), ">=", attribute.value());
            case "LESS_EQUALS" -> String.format("%s %s %s", attribute.name(), "<=", attribute.value());
            case "GREATER" -> String.format("%s %s %s", attribute.name(), ">", attribute.value());
            case "LESS" -> String.format("%s %s %s", attribute.name(), "<", attribute.value());
            case "REGEX" -> String.format("%s %s '%%%s%%'", attribute.name(), "LIKE", attribute.value());
            default -> throw new RuntimeException("Operator not supported");
        };
        logger.log(Level.INFO, () -> "Sending query to external service... " + statement);
        return true;
    }

    public boolean sendQueryIf(Attribute attribute) {
        String statement;
        switch (attribute.operator()) {
            case "EQUALS":
                statement = String.format("%s %s %s", attribute.name(), "==", attribute.value());
                break;
            case "NOT_EQUALS":
                statement = String.format("%s %s %s", attribute.name(), "!=", attribute.value());
                break;
            case "GREATER_EQUALS":
                statement = String.format("%s %s %s", attribute.name(), ">=", attribute.value());
                break;
            case "LESS_EQUALS":
                statement = String.format("%s %s %s", attribute.name(), "<=", attribute.value());
                break;
            case "GREATER":
                statement = String.format("%s %s %s", attribute.name(), ">", attribute.value());
                break;
            case "LESS":
                statement = String.format("%s %s %s", attribute.name(), "<", attribute.value());
                break;
            case "REGEX":
                statement = String.format("%s %s '%%%s%%'", attribute.name(), "LIKE", attribute.value());
                break;
            default:
                throw new RuntimeException("Operator not supported");
        }
        logger.log(Level.INFO, () -> "Sending query to external service... " + statement);
        return true;
    }


}

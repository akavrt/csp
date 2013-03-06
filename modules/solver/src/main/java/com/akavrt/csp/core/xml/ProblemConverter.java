package com.akavrt.csp.core.xml;

import com.akavrt.csp.core.Order;
import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.ProblemBuilder;
import com.akavrt.csp.core.Roll;
import com.akavrt.csp.core.metadata.ProblemMetadata;
import com.akavrt.csp.xml.XmlConverter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.jdom2.Element;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author Victor Balabanov <akavrt@gmail.com>
 */
public class ProblemConverter implements XmlConverter<Problem> {
    private final static String ORDER_ID_TEMPLATE = "order%d";
    private final static String ROLL_ID_TEMPLATE = "roll%d";

    @Override
    public Element export(Problem problem) {
        Element problemElm = new Element(XmlTags.PROBLEM);

        // converting metadata
        if (problem.getMetadata() != null) {
            Element metadataElm = problem.getMetadata().save();
            problemElm.addContent(metadataElm);
        }

        // converting constraints
        if (problem.getAllowedCutsNumber() > 0) {
            Element constraintElm = new Element(XmlTags.CONSTRAINTS);
            problemElm.addContent(constraintElm);

            Element allowedCutsElm = new Element(XmlTags.ALLOWED_CUTS);
            allowedCutsElm.setText(Integer.toString(problem.getAllowedCutsNumber()));
            constraintElm.addContent(allowedCutsElm);
        }

        // converting orders
        if (problem.getOrders() != null && problem.getOrders().size() > 0) {
            Element ordersElm = prepareOrders(problem);
            problemElm.addContent(ordersElm);
        }

        // converting rolls
        if (problem.getRolls() != null && problem.getRolls().size() > 0) {
            Element rollsElm = prepareRolls(problem);
            problemElm.addContent(rollsElm);
        }

        return problemElm;
    }

    @Override
    public Problem extract(Element rootElm) {
        ProblemBuilder builder = new ProblemBuilder();

        // process metadata
        Element metadataElm = rootElm.getChild(XmlTags.METADATA);
        if (metadataElm != null) {
            ProblemMetadata metadata = new ProblemMetadata();
            metadata.load(metadataElm);

            builder.setMetadata(metadata);
        }

        // process constraints
        Element constraintsElm = rootElm.getChild(XmlTags.CONSTRAINTS);
        if (constraintsElm != null) {
            int allowedCuts = Utils.getIntegerFromText(constraintsElm,
                                                       XmlTags.ALLOWED_CUTS);
            builder.setAllowedCutsNumber(allowedCuts);
        }

        // process list of orders
        Element ordersElm = rootElm.getChild(XmlTags.ORDERS);
        if (ordersElm != null) {
            retrieveOrders(ordersElm, builder);
        }

        // process list of rolls
        Element rollsElm = rootElm.getChild(XmlTags.ROLLS);
        if (rollsElm != null) {
            retrieveRolls(rollsElm, builder);
        }

        return builder.build();
    }

    private Element prepareOrders(Problem problem) {
        // sort orders using user-defined id's
        List<Order> orders = Lists.newArrayList(problem.getOrders());
        Collections.sort(orders, new Comparator<Order>() {
            @Override
            public int compare(Order lhs, Order rhs) {
                return lhs.getId().compareTo(rhs.getId());
            }
        });

        Element ordersElm = new Element(XmlTags.ORDERS);
        OrderConverter orderConverter = new OrderConverter();
        for (Order order : orders) {
            Element orderElm = orderConverter.export(order);
            ordersElm.addContent(orderElm);
        }

        return ordersElm;
    }

    private Element prepareRolls(Problem problem) {
        // group rolls
        List<Roll> rolls = problem.getRolls();
        Map<String, RollGroup> groups = Maps.newHashMap();
        for (Roll roll : rolls) {
            String id = roll.getId();
            RollGroup group = groups.get(id);
            if (group == null) {
                // first occurrence
                group = new RollGroup(roll);
                groups.put(id, group);
            } else {
                // no need to create new group
                group.incQuantity();
            }
        }

        // sort rolls using user-defined id's
        List<RollGroup> sorted = Lists.newArrayList(groups.values());
        Collections.sort(sorted, new Comparator<RollGroup>() {
            @Override
            public int compare(RollGroup lhs, RollGroup rhs) {
                return lhs.getRoll().getId().compareTo(rhs.getRoll().getId());
            }
        });

        Element rollsElm = new Element(XmlTags.ROLLS);
        RollConverter rollConverter = new RollConverter();
        for (RollGroup group : sorted) {
            Element rollElm = rollConverter.export(group.getRoll());

            int quantity = group.getQuantity();
            if (quantity > 1) {
                rollElm.setAttribute(XmlTags.QUANTITY, Integer.toString(quantity));
            }

            rollsElm.addContent(rollElm);
        }

        return rollsElm;
    }

    private void retrieveOrders(Element ordersElm, ProblemBuilder builder) {
        OrderConverter converter = new OrderConverter();
        int i = 0;
        for (Element orderElm : ordersElm.getChildren(XmlTags.ORDER)) {
            Order order = converter.extract(orderElm);

            if (order.isValid()) {
                if (order.getId() == null) {
                    String orderId = String.format(ORDER_ID_TEMPLATE, ++i);
                    order = new Order(orderId, order.getLength(), order.getWidth());
                }

                builder.addOrder(order);
            }
        }
    }

    private void retrieveRolls(Element rollsElm, ProblemBuilder builder) {
        RollConverter converter = new RollConverter();
        int i = 0;
        for (Element rollElm : rollsElm.getChildren(XmlTags.ROLL)) {
            Roll roll = converter.extract(rollElm);

            if (roll.isValid()) {
                if (roll.getId() == null) {
                    String rollId = String.format(ROLL_ID_TEMPLATE, ++i);
                    roll = new Roll(rollId, roll.getLength(), roll.getWidth());
                }

                int quantity = Utils.getIntegerFromAttribute(rollElm, XmlTags.QUANTITY, 1);
                if (quantity > 1) {
                    builder.addRolls(roll, quantity);
                } else {
                    builder.addRoll(roll);
                }
            }
        }
    }

    private interface XmlTags {
        String PROBLEM = "problem";
        String METADATA = "metadata";
        String CONSTRAINTS = "constraints";
        String ALLOWED_CUTS = "allowed-cuts";
        String ORDERS = "orders";
        String ORDER = "order";
        String QUANTITY = "quantity";
        String ROLLS = "rolls";
        String ROLL = "roll";
    }

    private static class RollGroup {
        private Roll roll;
        private int quantity;

        public RollGroup(Roll roll) {
            this.roll = roll;
            quantity = 1;
        }

        public Roll getRoll() {
            return roll;
        }

        public int getQuantity() {
            return quantity;
        }

        public void incQuantity() {
            quantity++;
        }
    }
}
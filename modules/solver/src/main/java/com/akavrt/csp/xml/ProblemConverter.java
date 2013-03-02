package com.akavrt.csp.xml;

import com.akavrt.csp.core.Order;
import com.akavrt.csp.core.Problem;
import com.akavrt.csp.core.Roll;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.jdom2.Element;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * User: akavrt
 * Date: 02.03.13
 * Time: 23:50
 */
public class ProblemConverter implements XmlConverter<Problem> {

    @Override
    public Element convert(Problem problem) {
        Element problemElm = new Element(ProblemTags.PROBLEM);

        if (problem.getAllowedCutsNumber() > 0) {
            Element constraintElm = new Element(ProblemTags.CONSTRAINTS);
            problemElm.addContent(constraintElm);

            Element allowedCutsElm = new Element(ProblemTags.ALLOWED_CUTS);
            allowedCutsElm.setText(Integer.toString(problem.getAllowedCutsNumber()));
            constraintElm.addContent(allowedCutsElm);
        }

        if (problem.getOrders() != null && problem.getOrders().size() > 0) {
            // sort orders using user-defined id's
            List<Order> orders = Lists.newArrayList(problem.getOrders());
            Collections.sort(orders, new Comparator<Order>() {
                @Override
                public int compare(Order lhs, Order rhs) {
                    return lhs.getId().compareTo(rhs.getId());
                }
            });

            Element ordersElm = new Element(ProblemTags.ORDERS);
            problemElm.addContent(ordersElm);
            OrderConverter orderConverter = new OrderConverter();
            for (Order order : orders) {
                Element orderElm = orderConverter.convert(order);
                ordersElm.addContent(orderElm);
            }
        }

        if (problem.getRolls() != null && problem.getRolls().size() > 0) {
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

            Element rollsElm = new Element(ProblemTags.ROLLS);
            problemElm.addContent(rollsElm);
            RollConverter rollConverter = new RollConverter();
            for (RollGroup group : sorted) {
                Element rollElm = rollConverter.convert(group.getRoll());

                int quantity = group.getQuantity();
                if (quantity > 1) {
                    rollElm.setAttribute(RollConverter.RollTags.QUANTITY,
                                         Integer.toString(quantity));
                }

                rollsElm.addContent(rollElm);
            }
        }

        return problemElm;
    }

    public interface ProblemTags {
        String PROBLEM = "problem";
        String CONSTRAINTS = "constraints";
        String ALLOWED_CUTS = "allowed-cuts";
        String ORDERS = "orders";
        String ROLLS = "rolls";
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
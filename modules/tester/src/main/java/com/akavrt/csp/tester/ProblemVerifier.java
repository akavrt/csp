package com.akavrt.csp.tester;

import com.akavrt.csp.utils.Utils;
import com.akavrt.csp.xml.XmlUtils;
import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * User: akavrt
 * Date: 08.04.13
 * Time: 13:50
 */
public class ProblemVerifier {
    private static final Logger LOGGER = LogManager.getLogger(ProblemVerifier.class);
    private Map<String, Order> oldFormatOrders;
    private Map<String, Roll> oldFormatRolls;
    private Map<String, Order> newFormatOrders;
    private Map<String, Roll> newFormatRolls;

    public static void main(String[] args) {
        String oldFormatBasePath = "/Users/akavrt/Thesis/data/1.5d/data_tubes";
        String newFormatBasePath = "/Users/akavrt/Development/source/csp/data/production";
        String problemFileNameTemplate = "tubes_%02d.xml";

        boolean isErrorFound = false;
        for (int i = 1; i <= 18; i++) {
            String problemFileName = String.format(problemFileNameTemplate, i);
            boolean isMatching = new ProblemVerifier().compare(oldFormatBasePath, newFormatBasePath,
                                                               problemFileName);

            LOGGER.info("Problem {} definition matches: {}", problemFileName, isMatching);

            isErrorFound = isErrorFound || !isMatching;
        }

        LOGGER.info("Errors found: {}", isErrorFound);
    }

    private boolean compare(String oldFormatBasePath, String newFormatBasePath,
                            String problemFileName) {
        LOGGER.debug("Comparing {}", problemFileName);

        String oldFormatProblemPath = new File(oldFormatBasePath, problemFileName).getPath();
        String newFormatProblemPath = new File(newFormatBasePath, problemFileName).getPath();

        return compare(oldFormatProblemPath, newFormatProblemPath);
    }

    private boolean compare(String oldFormatProblemPath, String newFormatProblemPath) {
        oldFormatOrders = null;
        oldFormatRolls = null;
        newFormatOrders = null;
        newFormatRolls = null;

        loadOldFormatProblem(oldFormatProblemPath);
        loadNewFormatProblem(newFormatProblemPath);

        // compare orders
        if (oldFormatOrders == null ^ newFormatOrders == null) {
            LOGGER.debug("One of the order lists is empty.");
            return false;
        }

        if (oldFormatOrders != null) {
            if (oldFormatOrders.size() != newFormatOrders.size()) {
                LOGGER.debug("Number of orders doesn't match.");
                return false;
            }

            for (Order oldOrder : oldFormatOrders.values()) {
                String id = oldOrder.id;
                Order newOrder = newFormatOrders.get(id);

                if (newOrder == null) {
                    LOGGER.debug("Can't find matching order with id = '{}'.", id);
                    return false;
                }

                if (oldOrder.width != newOrder.width) {
                    LOGGER.debug("Order with id = '{}', width doesn't match.", id);
                    return false;
                }

                if (oldOrder.length != newOrder.length) {
                    LOGGER.debug("Order with id = '{}', length doesn't match.", id);
                    return false;
                }
            }
        }

        // compare rolls
        if (oldFormatRolls == null ^ newFormatRolls == null) {
            LOGGER.debug("One of the roll lists is empty.");
            return false;
        }

        if (oldFormatRolls != null) {
            if (oldFormatRolls.size() != newFormatRolls.size()) {
                LOGGER.debug("Number of rolls doesn't match.");
                return false;
            }

            for (Roll oldRoll : oldFormatRolls.values()) {
                String id = oldRoll.id;
                Roll newRoll = newFormatRolls.get(id);

                if (newRoll == null) {
                    LOGGER.debug("Can't find matching roll with id = '{}'.", id);
                    return false;
                }

                if (oldRoll.width != newRoll.width) {
                    LOGGER.debug("Roll with id = '{}', width doesn't match.", id);
                    return false;
                }

                if (oldRoll.length != newRoll.length) {
                    LOGGER.debug("Roll with id = '{}', length doesn't match.", id);
                    return false;
                }

                if (oldRoll.quantity != newRoll.quantity) {
                    LOGGER.debug("Roll with id = '{}', quantity doesn't match.", id);
                    return false;
                }
            }
        }

        return true;
    }

    private void loadOldFormatProblem(String path) {
        LOGGER.debug("Loading old problem from '{}'.", path);

        File file = new File(path);
        try {
            SAXBuilder sax = new SAXBuilder();
            Document doc = sax.build(file);
            Element nestingElm = doc.getRootElement();
            Element problemElm = nestingElm.getChild(XmlTags.PROBLEM);
            if (problemElm != null) {
                Element ordersElm = problemElm.getChild(XmlTags.ORDERS);
                if (ordersElm != null) {
                    oldFormatOrders = loadOrders(ordersElm, true);
                } else {
                    LOGGER.debug("<{}> element wasn't found.", XmlTags.ORDERS);
                }

                Element rollsElm = problemElm.getChild(XmlTags.ROLLS);
                if (rollsElm != null) {
                    oldFormatRolls = loadRolls(rollsElm, true);
                } else {
                    LOGGER.debug("<{}> element wasn't found.", XmlTags.ROLLS);
                }
            } else {
                LOGGER.debug("<{}> element wasn't found.", XmlTags.PROBLEM);
            }
        } catch (JDOMException e) {
            LOGGER.catching(e);
        } catch (IOException e) {
            LOGGER.catching(e);
        }
    }

    private void loadNewFormatProblem(String path) {
        LOGGER.debug("Loading new problem from '{}'.", path);

        File file = new File(path);
        try {
            SAXBuilder sax = new SAXBuilder();
            Document doc = sax.build(file);
            Element cspElm = doc.getRootElement();
            Element problemElm = cspElm.getChild(XmlTags.PROBLEM);
            if (problemElm != null) {
                Element ordersElm = problemElm.getChild(XmlTags.ORDERS);
                if (ordersElm != null) {
                    newFormatOrders = loadOrders(ordersElm, false);
                } else {
                    LOGGER.debug("<{}> element wasn't found.", XmlTags.ORDERS);
                }

                Element rollsElm = problemElm.getChild(XmlTags.ROLLS);
                if (rollsElm != null) {
                    newFormatRolls = loadRolls(rollsElm, false);
                } else {
                    LOGGER.debug("<{}> element wasn't found.", XmlTags.ROLLS);
                }
            } else {
                LOGGER.debug("<{}> element wasn't found.", XmlTags.PROBLEM);
            }
        } catch (JDOMException e) {
            LOGGER.catching(e);
        } catch (IOException e) {
            LOGGER.catching(e);
        }
    }

    private Map<String, Order> loadOrders(Element ordersElm, boolean oldProblem) {
        Map<String, Order> orders = Maps.newHashMap();

        List<Element> orderElms = ordersElm.getChildren(XmlTags.ORDER);
        boolean addLeadingZero = orderElms.size() > 9 && oldProblem;

        for (Element element : orderElms) {
            String id = element.getAttributeValue(XmlTags.ID);

            Element stripElm = element.getChild(XmlTags.STRIP);
            if (!Utils.isEmpty(id) && stripElm != null) {
                double length = XmlUtils.getDoubleFromText(stripElm, XmlTags.LENGTH, 0);
                double width = XmlUtils.getDoubleFromText(stripElm, XmlTags.WIDTH, 0);

                if (addLeadingZero) {
                    String indexString = id.replace("order", "");
                    int index = Integer.parseInt(indexString);
                    String newId = String.format("order%02d", index);
                    LOGGER.debug("Replacing old id '{}' with new one '{}'", id, newId);
                    id = newId;
                }

                if (length > 0 && width > 0) {
                    orders.put(id, new Order(id, length, width));
                } else {
                    LOGGER.debug("Wrong definition of the order with id '{}'.", id);
                }
            } else {
                LOGGER.debug("<{}> element or order id wasn't found.", XmlTags.STRIP);
            }
        }

        return orders;
    }

    private Map<String, Roll> loadRolls(Element rollsElm, boolean oldProblem) {
        Map<String, Roll> rolls = Maps.newHashMap();

        List<Element> rollElms = rollsElm.getChildren(XmlTags.ROLL);
        boolean addLeadingZero = rollElms.size() > 9 && oldProblem;

        for (Element element : rollElms) {
            String id = element.getAttributeValue(XmlTags.ID);
            int quantity = XmlUtils.getIntegerFromAttribute(element, XmlTags.QUANTITY, 1);

            Element stripElm = element.getChild(XmlTags.STRIP);
            if (!Utils.isEmpty(id) && stripElm != null) {
                double length = XmlUtils.getDoubleFromText(stripElm, XmlTags.LENGTH, 0);
                double width = XmlUtils.getDoubleFromText(stripElm, XmlTags.WIDTH, 0);

                if (addLeadingZero) {
                    String indexString = id.replace("roll", "");
                    int index = Integer.parseInt(indexString);
                    String newId = String.format("roll%02d", index);
                    LOGGER.debug("Replacing old id '{}' with new one '{}'", id, newId);
                    id = newId;
                }

                if (length > 0 && width > 0) {
                    rolls.put(id, new Roll(id, length, width, quantity));
                } else {
                    LOGGER.debug("Wrong definition of the roll with id '{}'.", id);
                }
            } else {
                LOGGER.debug("<{}> element or roll id wasn't found.", XmlTags.STRIP);
            }
        }

        return rolls;
    }

    private interface XmlTags {
        String PROBLEM = "problem";
        String ORDERS = "orders";
        String ORDER = "order";
        String ROLLS = "rolls";
        String ROLL = "roll";
        String STRIP = "strip";
        String ID = "id";
        String WIDTH = "width";
        String LENGTH = "length";
        String QUANTITY = "quantity";
    }

    private static class Order {
        public final String id;
        public final double length;
        public final double width;

        public Order(String id, double length, double width) {
            this.id = id;
            this.length = length;
            this.width = width;
        }
    }

    private static class Roll {
        public final String id;
        public final double length;
        public final double width;
        public final int quantity;

        public Roll(String id, double length, double width, int quantity) {
            this.id = id;
            this.length = length;
            this.width = width;
            this.quantity = quantity;
        }
    }
}
package org.powerbot.game.api.methods.tab;

import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.input.Keyboard;
import org.powerbot.game.api.methods.widget.Lobby;
import org.powerbot.game.api.wrappers.widget.Widget;
import org.powerbot.game.api.wrappers.widget.WidgetChild;

public class FriendChat {

    private static final Widget MAIN_WIDGET_GAME = Widgets.get(1109);
    private static final Widget MAIN_WIDGET_LOBBY = Widgets.get(589);

    public enum Rank {

        UNRANKED, SMILY, RECRUIT, CORPORAL, SERGEANT, LIEUTENANT, CAPTAIN, GENERAL, OWNER
    }

    public static boolean openTab() {
        if (Lobby.isOpen()) {
            return Lobby.Tab.FRIENDS_CHAT.open();
        } else if (Game.isLoggedIn()) {
            return Tabs.FRIENDS_CHAT.open();
        }
        return false;
    }

    public static String getChatTopic() {
        if (Lobby.isOpen()) {
            if (!Lobby.Tab.FRIENDS_CHAT.isOpen()) {
                openTab();
            }
            final String topic = MAIN_WIDGET_LOBBY.getChild(19).getText().trim();
            return normalize(topic);
        } else if (Game.isLoggedIn()) {
            if (!Tabs.FRIENDS_CHAT.isOpen()) {
                openTab();
            }
            final String text = MAIN_WIDGET_GAME.getChild(1).getText();
            final String topic = text.substring(text.indexOf(">") + 1, text.indexOf("<br>"));
            return normalize(topic);
        }
        return null;
    }

    public static String getChatName() {
        if (Lobby.isOpen()) {
            if (!Lobby.Tab.FRIENDS_CHAT.isOpen()) {
                openTab();
            }
            final String owner = MAIN_WIDGET_LOBBY.getChild(20).getText().trim();
            return normalize(owner);
        } else if (Game.isLoggedIn()) {
            if (!Tabs.FRIENDS_CHAT.isOpen()) {
                openTab();
            }
            final String text = MAIN_WIDGET_GAME.getChild(1).getText();
            final String owner = text.substring(text.lastIndexOf(">") + 1);
            return normalize(owner);
        }
        return null;
    }

    private static String normalize(final String text) {
        final StringBuilder sb = new StringBuilder(text.length());
        final char[] chars = text.toCharArray();
        for (final char c : chars) {
            if (Character.isLetterOrDigit(c)) {
                sb.append(c);
            } else if (c == '-' || c == '_') {
                sb.append(c);
            } else {
                sb.append(' ');
            }
        }
        return sb.toString();
    }

    public static String[] getUsers() {
        if (isInChat()) {
            if (Lobby.isOpen()) {
                if (!Lobby.Tab.FRIENDS_CHAT.isOpen()) {
                    openTab();
                }
                final WidgetChild uc = MAIN_WIDGET_LOBBY.getChild(55);
                final WidgetChild[] userLabels = uc.getChildren();
                final String[] names = new String[userLabels.length];
                for (int i = 0; i < userLabels.length; i++) {
                    names[i] = normalize(userLabels[i].getText().trim());
                }
                return names;
            } else if (Game.isLoggedIn()) {
                if (!Tabs.FRIENDS_CHAT.isOpen()) {
                    openTab();
                }
                final WidgetChild uc = MAIN_WIDGET_GAME.getChild(5);
                final WidgetChild[] userLabels = uc.getChildren();
                final String[] names = new String[userLabels.length];
                for (int i = 0; i < userLabels.length; i++) {
                    names[i] = normalize(userLabels[i].getText().trim());
                }
                return names;
            }
        }
        return null;
    }

    public static boolean containsUser(final String name) {
        if (isInChat()) {
            if (Lobby.isOpen()) {
                if (!Lobby.Tab.FRIENDS_CHAT.isOpen()) {
                    openTab();
                }
                final String[] users = getUsers();
                for (final String user : users) {
                    if (user.equalsIgnoreCase(normalize(name))) {
                        return true;
                    }
                }
            } else if (Game.isLoggedIn()) {
                if (!Tabs.FRIENDS_CHAT.isOpen()) {
                    openTab();
                }
                final String[] users = getUsers();
                for (final String user : users) {
                    if (user.equalsIgnoreCase(normalize(name))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static Rank getRank(final String name) {
        WidgetChild rc = null;
        if (Lobby.isOpen()) {
            if (!Lobby.Tab.FRIENDS_CHAT.isOpen()) {
                openTab();
            }
            rc = MAIN_WIDGET_LOBBY.getChild(56);
        } else if (Game.isLoggedIn()) {
            if (!Tabs.FRIENDS_CHAT.isOpen()) {
                openTab();
            }
            rc = MAIN_WIDGET_GAME.getChild(6);
        }
        if (isInChat()) {
            final WidgetChild[] ranks = rc.getChildren();
            final String[] names = getUsers();
            for (int i = 0; i < names.length; i++) {
                if (normalize(names[i].trim()).equalsIgnoreCase(normalize(name.trim()))) {
                    final int rankId = ranks[i].getTextureId();
                    switch (rankId) {
                        case -1:
                            return Rank.UNRANKED;
                        case 1004:
                            return Rank.SMILY;
                        case 6226:
                            return Rank.RECRUIT;
                        case 6225:
                            return Rank.CORPORAL;
                        case 6224:
                            return Rank.SERGEANT;
                        case 6232:
                            return Rank.LIEUTENANT;
                        case 6233:
                            return Rank.CAPTAIN;
                        case 6231:
                            return Rank.GENERAL;
                        case 6227:
                            return Rank.OWNER;
                    }
                }
            }
        }
        return Rank.UNRANKED;
    }

    public static void join(final String name) {
        if (Lobby.isOpen()) {
            if (!Lobby.Tab.FRIENDS_CHAT.isOpen()) {
                openTab();
            }
            final WidgetChild button = MAIN_WIDGET_LOBBY.getChild(41);
            if (button.getText().equals("Join Chat Channel")) {
                if (button.interact("Join")) {
                    Keyboard.sendText(name, true);
                }
            }
        } else if (Game.isLoggedIn()) {
            if (!Tabs.FRIENDS_CHAT.isOpen()) {
                openTab();
            }
            final WidgetChild button = MAIN_WIDGET_GAME.getChild(27);
            if (button.getTextureId() == 6242) {
                if (button.interact("Join")) {
                    Keyboard.sendText(name, true);
                }
            }
        }
    }

    public static void leave() {
        if (Lobby.isOpen()) {
            if (!Lobby.Tab.FRIENDS_CHAT.isOpen()) {
                openTab();
            }
            final WidgetChild button = MAIN_WIDGET_LOBBY.getChild(41);
            if (button.getText().equals("Leave chat channel")) {
                button.interact("Leave");
            }
        } else if (Game.isLoggedIn()) {
            if (!Tabs.FRIENDS_CHAT.isOpen()) {
                openTab();
            }
            final WidgetChild button = MAIN_WIDGET_GAME.getChild(27);
            if (button.getTextureId() == 6243) {
                button.interact("Leave");
            }
        }
    }

    public static boolean isInChat() {
        if (Lobby.isOpen()) {
            if (!Lobby.Tab.FRIENDS_CHAT.isOpen()) {
                openTab();
            }
            return !getChatName().equals("None") && !getChatTopic().equals("Not in chat");
        } else if (Game.isLoggedIn()) {
            if (!Tabs.FRIENDS_CHAT.isOpen()) {
                openTab();
            }
            final WidgetChild button = MAIN_WIDGET_GAME.getChild(27);
            return button.getTextureId() == 6243;
        }
        return false;
    }
}

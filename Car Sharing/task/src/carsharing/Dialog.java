package carsharing;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.Supplier;

public class Dialog {
    static final Scanner SCANNER = new Scanner(System.in);

    static String readln() {
        return SCANNER.nextLine();
    }

    public void run() {
        State state = main;
        while (state != null) {
            state = state.get();
        }
    }

    Menu main = new Menu(
            new MenuItem(1, "Login as a manager", () -> this.managerMenu),
            new MenuItem(0, "Exit", null)
    );
    Menu managerMenu = new Menu(
            new MenuItem(1, "Company list", () -> printAll(this.main)),
            new MenuItem(2, "Create a company", () -> addCompany(readln())),
            new MenuItem(0, "Back", main)

    );

    State addCompany(String name) {
        try {
            new Repository(DriverManager.getConnection("jdbc:h2:./src/carsharing/db/carsharing")).setSqlAddCompany(name);
            System.out.println("Company added");
        } catch (Exception ex) {
            ex.printStackTrace();

        }
        return managerMenu;
    }

    State printAll(State next) {
        try {
            List<String> c =
                    new Repository(DriverManager.getConnection("jdbc:h2:./src/carsharing/db/carsharing")).getAll();
            System.out.println(c);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return next;
    }

    interface State extends Supplier<State> {
    }

    record MenuItem(
            int id,
            String title,
            State option
    ) {
    }

    class Menu implements State {
        MenuItem[] items;

        public Menu(MenuItem... items) {
            this.items = items;
        }

        public State show() {
            for (var item : items) {
                System.out.printf("%d. %s%n", item.id, item.title);
            }
            int option;
            try {
                option = Integer.parseInt(readln());
                for (var item : items) {
                    if (item.id == option) {
                        return item.option;
                    }
                }
            } catch (NumberFormatException e) {
                //do nothing
            }
            return this;
        }

        @Override
        public State get() {
            return show();
        }
    }
}

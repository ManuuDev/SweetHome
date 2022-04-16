module org.shdevelopment {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.desktop;
    requires java.json.bind;
    requires java.sql;
    requires org.eclipse.yasson;
    //requires org.glassfish.java.json;
    requires java.naming;
    requires java.management;
    requires commons.dbutils;

    opens org.shdevelopment.Core to javafx.fxml;
    opens org.shdevelopment.Controllers to javafx.fxml;
    opens org.shdevelopment.SysInfo to javafx.fxml;
    opens org.shdevelopment.Structures to org.eclipse.yasson;
    opens org.shdevelopment.ContactManagement to javafx.fxml;
    opens org.shdevelopment.Server to javafx.fxml;

    exports org.shdevelopment.Structures;
    exports org.shdevelopment.Core;
    exports org.shdevelopment.ContactManagement;
    exports org.shdevelopment.Server;
}
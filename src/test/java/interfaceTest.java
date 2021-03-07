import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import org.testfx.framework.junit.ApplicationTest;

public class interfaceTest extends ApplicationTest {
    long sleepTime = 10000;
    Stage stage;
    FXMLLoader loader;
    /**
     * Will be called with {@code @Before} semantics, i. e. before each test method.
     */
    @Override
    public void start(Stage stage) {
        this.stage = stage;
    }
    //TODO Testear crypto

    /*@Test
    public void showMetadataTablePanel() {
        Platform.runLater(() -> {
            try {
                loader = new FXMLLoader();
                loader.setLocation(getClass().getClassLoader().getResource("interface/tableOfMetadata.fxml"));
                Parent parent = loader.load();
                stage.setScene(new Scene(parent));
                stage.show();

                FileReceptionController controller = loader.getController();
                TableView tableView = controller.getTableView();

                List metadataList = new ArrayList<FileInfo>();
                String name = "Archivo-";

                for(int i=0; i<30; i++){
                    FileInfo metadata = new FileInfo(name + i, new Random().nextInt(10000));
                    metadataList.add(metadata);
                }

                ObservableList<FileInfo> observableList = FXCollections.observableList(metadataList);

                tableView.setItems(observableList);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        sleep(sleepTime);
    }*/
}
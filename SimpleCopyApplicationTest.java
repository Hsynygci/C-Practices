
import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.launcher.AppletLauncher;
import org.assertj.swing.launcher.ApplicationLauncher;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static org.assertj.swing.launcher.ApplicationLauncher.*;
import static org.assertj.swing.finder.WindowFinder.findFrame;


public class SimpleCopyApplicationTest {
  private FrameFixture window;


  @BeforeClass
  public static void setUpOnce() {
    FailOnThreadViolationRepaintManager.install();
  }


  @BeforeMethod
  public void setUp() throws IOException, ClassNotFoundException, AWTException {
    String pathToJar = "D:\\env\\SwingUnitTest\\out\\artifacts\\SwingUnitTest_jar\\SwingUnitTest.jar";
    JarFile jarFile = new JarFile(pathToJar);
    Enumeration<JarEntry> e = jarFile.entries();

    URL[] urls = { new URL("jar:file:" + pathToJar+"!/") };
    URLClassLoader cl = URLClassLoader.newInstance(urls);

    while (e.hasMoreElements()) {
      JarEntry je = e.nextElement();
      if(!je.getName().equals("simpleguiapp.class")){
        continue;
      }
      // -6 because of .class
      String className = je.getName().substring(0,je.getName().length()-6);
      className = className.replace('/', '.');
      Class c = cl.loadClass(className);

      ApplicationLauncher.application(c).start();


      GenericTypeMatcher<JFrame> matcher = new GenericTypeMatcher<JFrame>(JFrame.class) {
        @Override
        protected boolean isMatching(JFrame frame) {
          return frame.getTitle() != null && frame.getTitle().startsWith("simpleguiapp") && frame.isShowing();
        }
      };
      window = findFrame(matcher).using(BasicRobot.robotWithCurrentAwtHierarchy());

//      AppletViewer viewer = AppletLauncher.applet(c).start();
////      applet = new FrameFixture(viewer);
////      applet.show();

//      applet.button("ok").click();
//      applet.label("text").requireText("Hello");

//      SimpleCopyApplication frame = GuiActionRunner.execute(() -> new SimpleCopyApplication());
//      window = new FrameFixture(frame);
//      window.show(); // shows the frame to test
    }


  }

  @Test
  public void shouldCopyTextInLabelWhenClickingButton() {
    window.textBox("textToCopy").enterText("Some random text");
    window.button("copyButton").click();
    window.label("copiedText").requireText("Some random text");
  }

  @AfterMethod
  public void tearDown() {
    window.cleanUp();
  }
}

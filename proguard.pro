-libraryjars <java.home>/lib/rt.jar
-libraryjars <java.home>/lib/jce.jar
-libraryjars <java.home>/lib/ext/jfxrt.jar
-libraryjars build/dependlib

-printusage dist/shrinking.output
-printmapping dist/out.map

-ignorewarnings

-keepattributes *Annotation*,EnclosingMethod
-keep class modal.** {
    void set*(***);
    *** get*();
}
-keepclassmembers class model.** {public <fields>;}

-keep public class * {
public static void main(java.lang.String[]);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keep public class org.apache.commons.logging.impl.** {*;}
-keep public class org.slf4j.** {*;}
-keep public class ch.** {*;}

-adaptresourcefilecontents **.fxml
-keepclassmembernames class * {
    @javafx.fxml.FXML *;
}




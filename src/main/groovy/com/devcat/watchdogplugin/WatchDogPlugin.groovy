package com.devcat.watchdogplugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler
import org.aspectj.tools.ajc.Main
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile

public class WatchDogPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        final def log = project.logger;
        def isAppPlugin = project.plugins.getPlugin(AppPlugin.class);
        def android = project.extensions.getByType(AppExtension.class);
        final def variants;
        log.debug("isAppPlugin = " + isAppPlugin);
        if (isAppPlugin) {
            variants = android.applicationVariants;
        }

        project.dependencies {
            //project.dependencies.add("compile", "org.greenrobot:eventbus:3.0.0")

            //configurationName project(path: ':projectA', configuration: 'someOtherConfiguration')
            //map.put("path", ":watchdog-runtime");
            //project.dependencies.add("compile", project.dependencies)

            // compile 'com.devcat:watchdog-runtime:1.0.3'
            project.dependencies.add("compile", "com.devcat:watchdog-runtime:1.0.3");
        }

        //在构建工程时，执行AspectJ编译对java字节码进行处理。
        variants.all { variant ->
            if (!variant.buildType.isDebuggable()) {
                log.debug("Skipping non-debuggable build type '${variant.buildType.name}'.")
                return;
            }


            JavaCompile javaCompile = variant.javaCompile
            javaCompile.doLast {
                String[] args = ["-showWeaveInfo",
                                 "-1.5",
                                 "-inpath", javaCompile.destinationDir.toString(),
                                 "-aspectpath", javaCompile.classpath.asPath,
                                 "-d", javaCompile.destinationDir.toString(),
                                 "-classpath", javaCompile.classpath.asPath,
                                 "-bootclasspath", project.android.bootClasspath.join(File.pathSeparator)]
                log.debug "ajc args: " + Arrays.toString(args)

                MessageHandler handler = new MessageHandler(true);
                new Main().run(args, handler);
                for (IMessage message : handler.getMessages(null, true)) {
                    switch (message.getKind()) {
                        case IMessage.ABORT:
                        case IMessage.ERROR:
                        case IMessage.FAIL:
                            log.error message.message, message.thrown
                            break;
                        case IMessage.WARNING:
                            log.warn message.message, message.thrown
                            break;
                        case IMessage.INFO:
                            log.info message.message, message.thrown
                            break;
                        case IMessage.DEBUG:
                            log.debug message.message, message.thrown
                            break;
                    }
                }
            }
        }

        /*variants.all { variant ->
            variant.outputs.each { output ->
                output.processManifest.each {  xml ->
                    log.info("xml.name = " + xml.name);
                }
            }
        }*/
        /*def isAppPlugin = project.plugins.withType(AppPlugin);
        def isLibraryPlugin = project.plugins.withType(LibraryPlugin);
        final def log = project.logger;
        log.info("isAppPlugin = " +  isAppPlugin);
        log.info("isLibraryPlugin = " +  isLibraryPlugin);
        if (!isAppPlugin && !isLibraryPlugin) {
            throw new IllegalStateException("Only allow Android or Android-library to apply this plug-in.");
        }


        final def variants;
        if (isAppPlugin) {
            variants = project.android.applicationVariants;
        } else {
            variants = project.android.libraryVariants;
        }
*/
       /*

       // project.extensions.create("watchdog", WatchDogExtension);



        project.dependencies {
           // debugCompile project(':watchdog-runtime')
            debugCompile 'org.aspectj:aspectjrt:1.8.9'
            //compile project(':watchdog-annotations')
        }

        //在构建工程时，执行AspectJ编译对java字节码进行处理。
        variants.all { variant ->
            if (!variant.buildType.isDebuggable()) {
                log.debug("Skipping non-debuggable build type '${variant.buildType.name}'.")
                return;
            } else if (!project.watchdog.enabled) {
                log.debug("WatchDog is not disabled.");
                return;
            }

            JavaCompile javaCompile = variant.javaCompile
            javaCompile.doLast {
                String[] args = ["-showWeaveInfo",
                                 "-1.5",
                                 "-inpath", javaCompile.destinationDir.toString(),
                                 "-aspectpath", javaCompile.classpath.asPath,
                                 "-d", javaCompile.destinationDir.toString(),
                                 "-classpath", javaCompile.classpath.asPath,
                                 "-bootclasspath", project.android.bootClasspath.join(File.pathSeparator)]
                log.debug "ajc args: " + Arrays.toString(args)

                MessageHandler handler = new MessageHandler(true);
                new Main().run(args, handler);
                for (IMessage message : handler.getMessages(null, true)) {
                    switch (message.getKind()) {
                        case IMessage.ABORT:
                        case IMessage.ERROR:
                        case IMessage.FAIL:
                            log.error message.message, message.thrown
                            break;
                        case IMessage.WARNING:
                            log.warn message.message, message.thrown
                            break;
                        case IMessage.INFO:
                            log.info message.message, message.thrown
                            break;
                        case IMessage.DEBUG:
                            log.debug message.message, message.thrown
                            break;
                    }
                }
            }
        }*/
    }
}
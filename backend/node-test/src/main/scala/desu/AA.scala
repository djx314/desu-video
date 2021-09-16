package desu

import com.eclipsesource.v8.V8

object AA extends App {
  val runtime = V8.createV8Runtime();
  val result = runtime.executeIntegerScript(
    ""
      + "var hello = 'hello, ';\n"
      + "var world = 'world!';\n"
      + "hello.concat(world).length;\n"
  )
  System.out.println(result)
  runtime.release()
}

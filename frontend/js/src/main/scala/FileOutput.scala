import com.thoughtworks.binding.Binding._
import com.thoughtworks.binding._
import org.lrng.binding.html
import org.scalajs.dom._
import org.scalajs.dom.html._

import scala.collection.mutable.ListBuffer
import scala.scalajs.js.annotation._

@JSExportTopLevel(name = "FileOutput")
object FileOutput {

  case class Contact(name: Var[String])

  @html
  def bindingList(contact: BindingSeq[Contact]): Binding[Div] = <div>{
    for (eachContact <- contact) yield {
      <div>{eachContact.name.value}</div>
    }
  }</div>

  @html
  def bindButton(contact: Vars[Contact]): Binding[Button] = <button onclick={
    event: Event =>
      contact.value.clear()
  }>清空</button>

  @html
  def all(contact: Vars[Contact]): Binding[Div] = <div>
    <div>{bindingList(contact)}</div>
    <div>{bindButton(contact)}</div>
  </div>

  @JSExport
  def main(node: Node): Unit = {
    val data =
      Vars(Contact(Var("Yang Bo")), Contact(Var("Yang Bo1")), Contact(Var("Yang Bo2")), Contact(Var("Yang Bo3")), Contact(Var("Yang Bo4")))
    html.render(node, all(data))
  }

}

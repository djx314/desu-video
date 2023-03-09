import com.yang_bo.html.*
import com.thoughtworks.binding.Binding
import com.thoughtworks.binding.Binding.{BindingSeq, Constants, Var, Vars}

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import org.scalajs.dom.*

@JSExportTopLevel(name = "FileOutput")
object FileOutput {

  val valueS = for (i <- 1 to 20) yield Contact(name = Var(s"$i+200"))

  case class Contact(name: Var[String])

  def bindingList(contact: BindingSeq[Contact]): Binding[org.scalajs.dom.Node] = html"""<div>${for (eachContact <- contact) yield {
      html"""<div>{eachContact.name.value}</div>"""
    }}</div>"""

  def bindButton(contact: Vars[Contact]): Binding[org.scalajs.dom.Node] = html"""<button onclick=${(event: org.scalajs.dom.Event) =>
      contact.value.clear()
      contact.value.appendAll(valueS)
    }>清空</button>"""

  def all(contact: Vars[Contact]): Binding[org.scalajs.dom.Node] = html"""<div>
    <div>${bindingList(contact)}</div>
    <div>${bindButton(contact)}</div>
  </div>"""

  @JSExport
  def main(node: org.scalajs.dom.Element): Unit = {
    val data =
      Vars(Contact(Var("Yang Bo")), Contact(Var("Yang Bo1")), Contact(Var("Yang Bo2")), Contact(Var("Yang Bo3")), Contact(Var("Yang Bo4")))
    render(node, all(data))
  }

}

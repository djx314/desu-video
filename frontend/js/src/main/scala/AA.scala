import com.yang_bo.html.*

import com.thoughtworks.binding.Binding
import Binding.{BindingSeq, Constants, Var, Vars}

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import org.scalajs.dom.{document, Event}
import org.scalajs.dom.html.*

/*@JSExportTopLevel(name = "AA")
object AA {

  case class Contact(name: Var[String], email: Var[String])

  def bindingButton(contact: Contact): Binding[org.scalajs.dom.Node] = html"""<button
    onclick=${(event: org.scalajs.dom.Event) => contact.name.value = "Modified Name"}
    >
     Modify the name
    </button>"""

  def bindingTr(contact: Contact): Binding[org.scalajs.dom.HTMLTableRowElement] = html"""<tr>
      <td>${contact.name.bind}</td>
      <td>${contact.email.bind}</td>
      <td>${bindingButton(contact).bind}</td>
    </tr>"""

  def bindingTable(contacts: BindingSeq[Contact]): Binding[org.scalajs.dom.Node] = html"""<table>
      <tbody>
    ${for (contact <- contacts) yield {
      bindingTr(contact).bind
    }}
      </tbody>
    </table>"""

  @JSExport
  def main(node: org.scalajs.dom.Element): Unit = {
    val data = Vars(Contact(Var("Yang Bo"), Var("yang.bo@rea-group.com")))
    render(node, bindingTable(data))
  }
}*/

@JSExportTopLevel(name = "AA")
object AA {

  case class Contact(name: Var[String], email: Var[String])

  def bindingButton(contact: Contact): Binding[Button] = {
    html"""<button
      onclick=${(event: Event) => contact.name.value = "Modified Name"}
    >
     Modify the name
    </button>"""
  }

  def bindingTr(contact: Contact): Binding[TableRow] = {
    html"""<tr>
      <td>${contact.name.bind}</td>
      <td>${contact.email.bind}</td>
      <td>${bindingButton(contact).bind}</td>
    </tr>"""
  }

  def bindingTable(contacts: BindingSeq[Contact]): Binding[Table] = {
    html"""<table>
      <tbody>
        ${for (contact <- contacts) yield {
        bindingTr(contact).bind
      }}
      </tbody>
    </table>"""
  }

  @JSExport
  def main(): Unit = {
    val data = Vars(Contact(Var("Yang Bo"), Var("yang.bo@rea-group.com")))
    render(document.body, bindingTable(data))
  }

}

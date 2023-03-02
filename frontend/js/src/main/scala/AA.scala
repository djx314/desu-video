import scala.scalajs.js.annotation._
import com.thoughtworks.binding._
import Binding._
import org.scalajs.dom._
import html._
import org.lrng.binding.html

@JSExportTopLevel(name = "AA")
object AA {

  case class Contact(name: Var[String], email: Var[String])

  @html
  def bindingButton(contact: Contact): Binding[Button] = {
    <button
      onclick={
      event: Event =>
        contact.name.value = "Modified Name"
    }
    >
     Modify the name
    </button>
  }

  @html
  def bindingTr(contact: Contact): Binding[TableRow] = {
    <tr>
      <td>{contact.name.bind}</td>
      <td>{contact.email.bind}</td>
      <td>{bindingButton(contact).bind}</td>
    </tr>
  }

  @html
  def bindingTable(contacts: BindingSeq[Contact]): Binding[Table] = {
    <table>
      <tbody>
        {
      for (contact <- contacts) yield {
        bindingTr(contact).bind
      }
    }
      </tbody>
    </table>
  }

  @JSExport
  def main(node: Node): Unit = {
    val data = Vars(Contact(Var("Yang Bo"), Var("yang.bo@rea-group.com")))
    html.render(node, bindingTable(data))
  }
}

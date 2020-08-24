/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers.register

import controllers.actions._
import controllers.actions.register.{ProtectorRequiredActionImpl, ProtectorRequiredRequest}
import forms.RemoveIndexFormProvider
import pages.QuestionPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents, Result}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import viewmodels.addAnother.ProtectorViewModel
import views.html.RemoveIndexView

import scala.concurrent.{ExecutionContext, Future}

trait RemoveIndexController extends FrontendBaseController with I18nSupport {
  val registrationsRepository: RegistrationsRepository
  val standardActionSets: StandardActionSets
  val protectorAction: ProtectorRequiredActionImpl
  val formProvider: RemoveIndexFormProvider
  val controllerComponents: MessagesControllerComponents
  val view: RemoveIndexView
  implicit val ec: ExecutionContext

  private val prefix = "removeProtector"

  def protectorAtIndex(index: Int): QuestionPage[ProtectorViewModel]

  def submitCall(index: Int, draftId: String): Call

  private def actions(index: Int, draftId: String) =
    standardActionSets.identifiedUserWithData(draftId) andThen protectorAction(protectorAtIndex(index), draftId)

  private def redirect(draftId: String): Result = Redirect(controllers.register.routes.AddAProtectorController.onPageLoad(draftId))

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId) {
    implicit request =>

      val form: Form[Boolean] = formProvider(prefix)

      Ok(view(form, draftId, index, name(request.protector), submitCall(index, draftId)))
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>

      val form: Form[Boolean] = formProvider(prefix)

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, draftId, index, name(request.protector), submitCall(index, draftId)))),

        remove => {
          if (remove) {
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.deleteAtPath(protectorAtIndex(index).path))
              _ <- registrationsRepository.set(updatedAnswers)
            } yield redirect(draftId)
          } else {
            Future.successful(redirect(draftId))
          }
        }
      )
  }

  private def name(protector: ProtectorViewModel)(implicit request: ProtectorRequiredRequest[AnyContent]): String = {
    protector.displayName match {
      case Some(name) => name
      case None => Messages(s"$prefix.default")
    }
  }
}

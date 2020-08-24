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

import config.FrontendAppConfig
import controllers.actions.{RequiredAnswer, RequiredAnswerActionProvider, StandardActionSets}
import forms.{AddAProtectorFormProvider, YesNoFormProvider}
import javax.inject.Inject
import models.Enumerable
import models.register.pages.AddAProtector.NoComplete
import navigation.Navigator
import pages.register.{AddAProtectorPage, AddAProtectorYesNoPage, TrustHasProtectorYesNoPage}
import play.api.Logger
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages, MessagesApi, MessagesProvider}
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.AddAProtectorViewHelper
import views.html.register.{AddAProtectorView, TrustHasProtectorYesNoView}

import scala.concurrent.{ExecutionContext, Future}

class AddAProtectorController @Inject()(
                                           override val messagesApi: MessagesApi,
                                           registrationsRepository: RegistrationsRepository,
                                           navigator: Navigator,
                                           standardActionSets: StandardActionSets,
                                           requiredAnswer: RequiredAnswerActionProvider,
                                           addAnotherFormProvider: AddAProtectorFormProvider,
                                           yesNoFormProvider: YesNoFormProvider,
                                           val controllerComponents: MessagesControllerComponents,
                                           addAnotherView: AddAProtectorView,
                                           yesNoView: TrustHasProtectorYesNoView,
                                           config: FrontendAppConfig
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController

  with I18nSupport with Enumerable.Implicits with AnyProtectors {

  private val addAnotherForm = addAnotherFormProvider()
  private val yesNoForm = yesNoFormProvider.withPrefix("trustHasProtectorYesNo")

  def trustHasProtectorAnswer(draftId: String) =
    requiredAnswer(RequiredAnswer(TrustHasProtectorYesNoPage, routes.TrustHasProtectorYesNoController.onPageLoad(draftId)))


  private def heading(count: Int)(implicit mp : MessagesProvider) = {
    count match {
      case 0 => Messages("addAProtector.heading")
      case 1 => Messages("addAProtector.singular.heading")
      case size => Messages("addAProtector.count.heading", size)
    }
  }

  def onPageLoad(draftId: String): Action[AnyContent] = standardActionSets.identifiedUserWithData(draftId).andThen(trustHasProtectorAnswer(draftId)) {
    implicit request =>

      val rows = new AddAProtectorViewHelper(request.userAnswers, draftId).rows

      val allProtectors = protectors(request.userAnswers)

      if(rows.count > 0) {
        val listOfMaxed = allProtectors.maxedOutOptions.map(_.messageKey)
        if(listOfMaxed.size == 1) {Logger.info(s"[AddAProtectorController] ${request.internalId} has maxed out protectors")}
        else {Logger.info(s"[AddAProtectorController] ${request.internalId} has not maxed out protectors")}
        Ok(addAnotherView(addAnotherForm, draftId, rows.inProgress, rows.complete, heading(rows.count), listOfMaxed))
      } else {
        Logger.info(s"[AddAProtectorController] ${request.internalId} has added no protectors")
        Ok(yesNoView(yesNoForm, draftId))
      }
  }

  def submitOne(draftId : String) : Action[AnyContent] = standardActionSets.identifiedUserWithData(draftId).andThen(trustHasProtectorAnswer(draftId)).async {
    implicit request =>
      yesNoForm.bindFromRequest().fold(
        (formWithErrors: Form[_]) => {
          Future.successful(
            BadRequest(yesNoView(formWithErrors, draftId))
          )
        },
        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AddAProtectorYesNoPage, value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AddAProtectorYesNoPage, draftId, updatedAnswers))
        }
      )
  }

  def submitAnother(draftId: String): Action[AnyContent] = standardActionSets.identifiedUserWithData(draftId).andThen(trustHasProtectorAnswer(draftId)).async {
    implicit request =>

      addAnotherForm.bindFromRequest().fold(
        (formWithErrors: Form[_]) => {

          val rows = new AddAProtectorViewHelper(request.userAnswers, draftId).rows
          val allProtectors = protectors(request.userAnswers)
          val listOfMaxed = allProtectors.maxedOutOptions.map(_.messageKey)

          Future.successful(BadRequest(
            addAnotherView(
              formWithErrors,
              draftId,
              rows.inProgress,
              rows.complete,
              heading(rows.count),
              listOfMaxed
            )
          ))
        },
        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AddAProtectorPage, value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AddAProtectorPage, draftId, updatedAnswers))
        }
      )
  }

  def submitComplete(draftId: String): Action[AnyContent] = standardActionSets.identifiedUserWithData(draftId).andThen(trustHasProtectorAnswer(draftId)).async {
    implicit request =>
      for {
        updatedAnswers <- Future.fromTry(request.userAnswers.set(AddAProtectorPage, NoComplete))
        _              <- registrationsRepository.set(updatedAnswers)
      } yield Redirect(Call("GET", config.registrationProgressUrl(draftId)))
  }

}

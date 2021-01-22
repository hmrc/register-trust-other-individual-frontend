/*
 * Copyright 2021 HM Revenue & Customs
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
import config.annotations.OtherIndividual
import controllers.actions.{RequiredAnswer, RequiredAnswerActionProvider, StandardActionSets}
import forms.{AddOtherIndividualFormProvider, YesNoFormProvider}
import javax.inject.Inject
import models.Enumerable
import models.register.pages.AddOtherIndividual.NoComplete
import navigation.Navigator
import pages.register.{AddOtherIndividualPage, AddOtherIndividualYesNoPage, TrustHasOtherIndividualYesNoPage}
import play.api.Logging
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages, MessagesApi, MessagesProvider}
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.AddOtherIndividualViewHelper
import views.html.register.{AddOtherIndividualView, TrustHasOtherIndividualYesNoView}

import scala.concurrent.{ExecutionContext, Future}

class AddOtherIndividualController @Inject()(
                                           override val messagesApi: MessagesApi,
                                           registrationsRepository: RegistrationsRepository,
                                           @OtherIndividual navigator: Navigator,
                                           standardActionSets: StandardActionSets,
                                           requiredAnswer: RequiredAnswerActionProvider,
                                           addAnotherFormProvider: AddOtherIndividualFormProvider,
                                           yesNoFormProvider: YesNoFormProvider,
                                           val controllerComponents: MessagesControllerComponents,
                                           addAnotherView: AddOtherIndividualView,
                                           yesNoView: TrustHasOtherIndividualYesNoView,
                                           config: FrontendAppConfig
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with Logging

  with I18nSupport with Enumerable.Implicits with AnyOtherIndividuals {

  private val addAnotherForm = addAnotherFormProvider()
  private val yesNoForm = yesNoFormProvider.withPrefix("trustHasOtherIndividualYesNo")

  def trustHasOtherIndividualAnswer(draftId: String) =
    requiredAnswer(RequiredAnswer(TrustHasOtherIndividualYesNoPage, routes.TrustHasOtherIndividualYesNoController.onPageLoad(draftId)))


  private def heading(count: Int)(implicit mp : MessagesProvider) = {
    count match {
      case 0 => Messages("addOtherIndividual.heading")
      case 1 => Messages("addOtherIndividual.singular.heading")
      case size => Messages("addOtherIndividual.count.heading", size)
    }
  }

  def onPageLoad(draftId: String): Action[AnyContent] = standardActionSets.identifiedUserWithData(draftId).andThen(trustHasOtherIndividualAnswer(draftId)) {
    implicit request =>

      val rows = new AddOtherIndividualViewHelper(request.userAnswers, draftId).rows

      val allOtherIndividuals = otherIndividuals(request.userAnswers)

      allOtherIndividuals.size match {
        case 0 =>
          logger.info(s"[Session ID: ${request.sessionId}] ${request.internalId} has added no other individuals")
          Ok(yesNoView(yesNoForm, draftId))
        case _ =>
          if (allOtherIndividuals.isMaxedOut) {
            logger.info(s"[Session ID: ${request.sessionId}] ${request.internalId} has maxed out otherIndividuals")
          } else {
            logger.info(s"[Session ID: ${request.sessionId}] ${request.internalId} has not maxed out otherIndividuals")
          }
          Ok(addAnotherView(
            addAnotherForm,
            draftId,
            rows.inProgress,
            rows.complete,
            heading(rows.count),
            allOtherIndividuals.isMaxedOut
          ))
      }
  }

  def submitOne(draftId : String) : Action[AnyContent] = standardActionSets.identifiedUserWithData(draftId).andThen(trustHasOtherIndividualAnswer(draftId)).async {
    implicit request =>
      yesNoForm.bindFromRequest().fold(
        (formWithErrors: Form[_]) => {
          Future.successful(
            BadRequest(yesNoView(formWithErrors, draftId))
          )
        },
        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AddOtherIndividualYesNoPage, value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AddOtherIndividualYesNoPage, draftId, updatedAnswers))
        }
      )
  }

  def submitAnother(draftId: String): Action[AnyContent] = standardActionSets.identifiedUserWithData(draftId).andThen(trustHasOtherIndividualAnswer(draftId)).async {
    implicit request =>

      addAnotherForm.bindFromRequest().fold(
        (formWithErrors: Form[_]) => {

          val rows = new AddOtherIndividualViewHelper(request.userAnswers, draftId).rows
          val allOtherIndividuals = otherIndividuals(request.userAnswers)

          Future.successful(BadRequest(
            addAnotherView(
              formWithErrors,
              draftId,
              rows.inProgress,
              rows.complete,
              heading(rows.count),
              allOtherIndividuals.isMaxedOut
            )
          ))
        },
        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AddOtherIndividualPage, value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AddOtherIndividualPage, draftId, updatedAnswers))
        }
      )
  }

  def submitComplete(draftId: String): Action[AnyContent] = standardActionSets.identifiedUserWithData(draftId).andThen(trustHasOtherIndividualAnswer(draftId)).async {
    implicit request =>
      for {
        updatedAnswers <- Future.fromTry(request.userAnswers.set(AddOtherIndividualPage, NoComplete))
        _              <- registrationsRepository.set(updatedAnswers)
      } yield Redirect(Call("GET", config.registrationProgressUrl(draftId)))
  }

}

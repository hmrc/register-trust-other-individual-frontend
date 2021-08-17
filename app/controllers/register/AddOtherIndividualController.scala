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
import controllers.actions.{RequiredAnswer, RequiredAnswerAction, RequiredAnswerActionProvider, StandardActionSets}
import forms.{AddOtherIndividualFormProvider, YesNoFormProvider}
import models.Status.Completed
import models.TaskStatus.TaskStatus
import models.register.pages.AddOtherIndividual
import models.register.pages.AddOtherIndividual.NoComplete
import models.requests.RegistrationDataRequest
import models.{Enumerable, TaskStatus, UserAnswers}
import navigation.Navigator
import pages.register.{AddOtherIndividualPage, AddOtherIndividualYesNoPage, TrustHasOtherIndividualYesNoPage}
import play.api.Logging
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages, MessagesApi, MessagesProvider}
import play.api.mvc._
import repositories.RegistrationsRepository
import services.TrustsStoreService
import uk.gov.hmrc.http.HttpVerbs.GET
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.{AddOtherIndividualViewHelper, RegistrationProgress}
import views.html.register.{AddOtherIndividualView, TrustHasOtherIndividualYesNoView}

import javax.inject.Inject
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
                                              config: FrontendAppConfig,
                                              trustsStoreService: TrustsStoreService,
                                              registrationProgress: RegistrationProgress
                                            )(implicit ec: ExecutionContext) extends FrontendBaseController with Logging
  with I18nSupport with Enumerable.Implicits with AnyOtherIndividuals {

  private val addAnotherForm = addAnotherFormProvider()
  private val yesNoForm = yesNoFormProvider.withPrefix("trustHasOtherIndividualYesNo")

  private def actions(draftId: String): ActionBuilder[RegistrationDataRequest, AnyContent] =
    standardActionSets.identifiedUserWithData(draftId).andThen(trustHasOtherIndividualAnswer(draftId))

  private def trustHasOtherIndividualAnswer(draftId: String): RequiredAnswerAction[Boolean] =
    requiredAnswer(RequiredAnswer(TrustHasOtherIndividualYesNoPage, routes.TrustHasOtherIndividualYesNoController.onPageLoad(draftId)))

  private def heading(count: Int)(implicit mp: MessagesProvider): String = {
    count match {
      case x if x <= 1 => Messages("addOtherIndividual.heading")
      case _ => Messages("addOtherIndividual.count.heading", count)
    }
  }

  private def setTaskStatus(draftId: String, userAnswers: UserAnswers, action: AddOtherIndividual)
                           (implicit hc: HeaderCarrier): Future[HttpResponse] = {
    val status = (action, registrationProgress.otherIndividualsStatus(userAnswers)) match {
      case (NoComplete, Some(Completed)) => TaskStatus.Completed
      case _ => TaskStatus.InProgress
    }
    setTaskStatus(draftId, status)
  }

  private def setTaskStatus(draftId: String, taskStatus: TaskStatus)
                           (implicit hc: HeaderCarrier): Future[HttpResponse] = {
    trustsStoreService.updateTaskStatus(draftId, taskStatus)
  }

  def onPageLoad(draftId: String): Action[AnyContent] = actions(draftId) {
    implicit request =>

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

          val rows = new AddOtherIndividualViewHelper(request.userAnswers, draftId).rows

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

  def submitOne(draftId: String): Action[AnyContent] = actions(draftId).async {
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
            _              <- setTaskStatus(draftId, if (value) TaskStatus.InProgress else TaskStatus.Completed)
          } yield Redirect(navigator.nextPage(AddOtherIndividualYesNoPage, draftId, updatedAnswers))
        }
      )
  }

  def submitAnother(draftId: String): Action[AnyContent] = actions(draftId).async {
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
            _              <- setTaskStatus(draftId, updatedAnswers, value)
          } yield Redirect(navigator.nextPage(AddOtherIndividualPage, draftId, updatedAnswers))
        }
      )
  }

  def submitComplete(draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>
      for {
        updatedAnswers <- Future.fromTry(request.userAnswers.set(AddOtherIndividualPage, NoComplete))
        _              <- registrationsRepository.set(updatedAnswers)
        _              <- setTaskStatus(draftId,updatedAnswers, NoComplete)
      } yield Redirect(Call(GET, config.registrationProgressUrl(draftId)))
  }

}

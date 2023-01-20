/*
 * Copyright 2023 HM Revenue & Customs
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

package controllers.register.individual

import controllers.actions._
import controllers.actions.register.OtherIndividualRequiredActionImpl
import controllers.register.RemoveIndexController
import forms.RemoveIndexFormProvider
import javax.inject.Inject
import pages.QuestionPage
import play.api.i18n.MessagesApi
import play.api.mvc.{Call, MessagesControllerComponents}
import repositories.RegistrationsRepository
import sections.OtherIndividual
import viewmodels.addAnother.OtherIndividualViewModel
import views.html.RemoveIndexView

import scala.concurrent.ExecutionContext

class RemoveOtherIndividualController @Inject()(
                                                 override val messagesApi: MessagesApi,
                                                 val registrationsRepository: RegistrationsRepository,
                                                 val standardActionSets: StandardActionSets,
                                                 val otherIndividualAction: OtherIndividualRequiredActionImpl,
                                                 val formProvider: RemoveIndexFormProvider,
                                                 val controllerComponents: MessagesControllerComponents,
                                                 val view: RemoveIndexView
                                               )(implicit val ec: ExecutionContext) extends RemoveIndexController {
  def otherIndividualAtIndex(index: Int): QuestionPage[OtherIndividualViewModel] = OtherIndividual(index)
  def submitCall(index: Int, draftId: String): Call = routes.RemoveOtherIndividualController.onSubmit(index, draftId)
}

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

package controllers.register.individual

import controllers.actions._
import controllers.actions.register.ProtectorRequiredActionImpl
import controllers.register.RemoveIndexController
import forms.RemoveIndexFormProvider
import javax.inject.Inject
import pages.QuestionPage
import play.api.i18n.MessagesApi
import play.api.mvc.{Call, MessagesControllerComponents}
import repositories.RegistrationsRepository
import sections.IndividualProtectorView
import viewmodels.addAnother.ProtectorViewModel
import views.html.RemoveIndexView

import scala.concurrent.ExecutionContext

class RemoveProtectorController @Inject()(
                                           override val messagesApi: MessagesApi,
                                           val registrationsRepository: RegistrationsRepository,
                                           val standardActionSets: StandardActionSets,
                                           val protectorAction: ProtectorRequiredActionImpl,
                                           val formProvider: RemoveIndexFormProvider,
                                           val controllerComponents: MessagesControllerComponents,
                                           val view: RemoveIndexView
                                         )(implicit val ec: ExecutionContext) extends RemoveIndexController {
  def protectorAtIndex(index: Int): QuestionPage[ProtectorViewModel] = IndividualProtectorView(index)
  def submitCall(index: Int, draftId: String): Call = routes.RemoveProtectorController.onSubmit(index, draftId)
}

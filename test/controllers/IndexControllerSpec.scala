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

package controllers

import base.SpecBase
import connectors.SubmissionDraftConnector
import models.{FullName, TaskStatus, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.{any, eq => mEq}
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.BeforeAndAfterEach
import pages.register.individual.NamePage
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.TrustsStoreService
import uk.gov.hmrc.http.HttpResponse

import scala.concurrent.Future

class IndexControllerSpec extends SpecBase with BeforeAndAfterEach {

  private val name: FullName = FullName("Joe", None, "Bloggs")
  private val submissionDraftConnector: SubmissionDraftConnector = mock[SubmissionDraftConnector]
  private val mockTrustsStoreService: TrustsStoreService = mock[TrustsStoreService]

  override protected def beforeEach(): Unit = {
    reset(mockTrustsStoreService)

    when(mockTrustsStoreService.updateTaskStatus(any(), any())(any(), any()))
      .thenReturn(Future.successful(HttpResponse(OK, "")))
  }

  "Index Controller" must {

    "pre-existing user answers" must {

      "redirect to add-to page if there is at least one in-progress or completed other individual" in {

        val userAnswers: UserAnswers = emptyUserAnswers
          .set(NamePage(0), name).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[TrustsStoreService].to(mockTrustsStoreService)
          )
          .build()

        when(registrationsRepository.get(any())(any())).thenReturn(Future.successful(Some(userAnswers)))
        when(submissionDraftConnector.getIsTrustTaxable(any())(any(), any())).thenReturn(Future.successful(true))

        val request = FakeRequest(GET, routes.IndexController.onPageLoad(fakeDraftId).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).get mustBe controllers.register.routes.AddOtherIndividualController.onPageLoad(fakeDraftId).url

        verify(mockTrustsStoreService).updateTaskStatus(mEq(draftId), mEq(TaskStatus.InProgress))(any(), any())

        application.stop()
      }

      "redirect to trust has other individuals yes no page if there are no in-progress or completed other individuals" in {

        val userAnswers: UserAnswers = emptyUserAnswers

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[TrustsStoreService].toInstance(mockTrustsStoreService)
          )
          .build()

        when(registrationsRepository.get(any())(any())).thenReturn(Future.successful(Some(userAnswers)))
        when(submissionDraftConnector.getIsTrustTaxable(any())(any(), any())).thenReturn(Future.successful(true))

        val request = FakeRequest(GET, routes.IndexController.onPageLoad(fakeDraftId).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).get mustBe controllers.register.routes.TrustHasOtherIndividualYesNoController.onPageLoad(fakeDraftId).url

        verify(mockTrustsStoreService).updateTaskStatus(mEq(draftId), mEq(TaskStatus.InProgress))(any(), any())

        application.stop()
      }

      "update value of isTaxable in user answers" in {

        reset(registrationsRepository)

        val userAnswers = emptyUserAnswers

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[TrustsStoreService].toInstance(mockTrustsStoreService)
          )
          .build()

        when(registrationsRepository.get(any())(any())).thenReturn(Future.successful(Some(userAnswers)))
        when(registrationsRepository.set(any())(any(), any())).thenReturn(Future.successful(true))
        when(submissionDraftConnector.getIsTrustTaxable(any())(any(), any())).thenReturn(Future.successful(true))

        val request = FakeRequest(GET, routes.IndexController.onPageLoad(fakeDraftId).url)

        route(application, request).value.map { _ =>
          val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
          verify(registrationsRepository).set(uaCaptor.capture)(any(), any())

          uaCaptor.getValue.isTaxable mustBe true

          application.stop()
        }
      }

    }

    "no pre-existing user answers" must {

      "instantiate new set of user answers" in {


          reset(registrationsRepository)

          val application = applicationBuilder(userAnswers = None)
            .overrides(bind[TrustsStoreService].toInstance(mockTrustsStoreService))
            .build()

          when(registrationsRepository.get(any())(any())).thenReturn(Future.successful(None))
          when(registrationsRepository.set(any())(any(), any())).thenReturn(Future.successful(true))
          when(submissionDraftConnector.getIsTrustTaxable(any())(any(), any())).thenReturn(Future.successful(false))

          val request = FakeRequest(GET, routes.IndexController.onPageLoad(fakeDraftId).url)

          route(application, request).value.map { _ =>
            val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
            verify(registrationsRepository).set(uaCaptor.capture)(any(), any())

            uaCaptor.getValue.draftId mustBe fakeDraftId
            uaCaptor.getValue.internalAuthId mustBe "id"

            application.stop()
          }



      }

    }

  }
}

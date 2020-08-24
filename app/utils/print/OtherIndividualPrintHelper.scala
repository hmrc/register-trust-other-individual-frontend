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

package utils.print

import com.google.inject.Inject
import controllers.register.individual.{routes => irts}
import models.UserAnswers
import pages.register.individual._
import play.api.i18n.Messages
import utils.countryOptions.CountryOptions
import viewmodels.{AnswerRow, AnswerSection}

class OtherIndividualPrintHelper @Inject()(answerRowConverter: AnswerRowConverter,
                                           countryOptions: CountryOptions
                                          ) {

  def printSection(userAnswers: UserAnswers, name: String, index: Int, draftId: String)(implicit messages: Messages): AnswerSection = {
    AnswerSection(
      Some(Messages("answerPage.section.individualOtherIndividual.subheading", index + 1)),
      answers(userAnswers, name, index, draftId)
    )
  }

  def checkDetailsSection(userAnswers: UserAnswers, name: String, index: Int, draftId: String)(implicit messages: Messages): AnswerSection = {
    AnswerSection(
      None,
      answers(userAnswers, name, index, draftId)
    )
  }

  def answers(userAnswers: UserAnswers, name: String, index: Int, draftId: String)
             (implicit messages: Messages): Seq[AnswerRow] = {
    val bound: answerRowConverter.Bound = answerRowConverter.bind(userAnswers, name, countryOptions)

    Seq(
      bound.nameQuestion(NamePage(index), "individualOtherIndividual.name", irts.NameController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(DateOfBirthYesNoPage(index), "individualOtherIndividual.dateOfBirthYesNo", irts.DateOfBirthYesNoController.onPageLoad(index, draftId).url),
      bound.dateQuestion(DateOfBirthPage(index), "individualOtherIndividual.dateOfBirth", irts.DateOfBirthController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(NationalInsuranceYesNoPage(index), "individualOtherIndividual.nationalInsuranceYesNo", irts.NationalInsuranceYesNoController.onPageLoad(index, draftId).url),
      bound.stringQuestion(NationalInsuranceNumberPage(index), "individualOtherIndividual.nationalInsuranceNumber", irts.NationalInsuranceNumberController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(AddressYesNoPage(index), "individualOtherIndividual.addressYesNo", irts.AddressYesNoController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(AddressUkYesNoPage(index), "individualOtherIndividual.addressUkYesNo", irts.AddressUkYesNoController.onPageLoad(index, draftId).url),
      bound.addressQuestion(UkAddressPage(index), "site.address.uk", irts.UkAddressController.onPageLoad(index, draftId).url),
      bound.addressQuestion(NonUkAddressPage(index), "site.address.international", irts.NonUkAddressController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(PassportDetailsYesNoPage(index), "individualOtherIndividual.passportDetailsYesNo", irts.PassportDetailsYesNoController.onPageLoad(index, draftId).url),
      bound.passportDetailsQuestion(PassportDetailsPage(index), "individualOtherIndividual.passportDetails", irts.PassportDetailsController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(IDCardDetailsYesNoPage(index), "individualOtherIndividual.idCardDetailsYesNo", irts.IDCardDetailsYesNoController.onPageLoad(index, draftId).url),
      bound.passportDetailsQuestion(IDCardDetailsPage(index), "individualOtherIndividual.idCardDetails", irts.IDCardDetailsController.onPageLoad(index, draftId).url)
    ).flatten
  }
}

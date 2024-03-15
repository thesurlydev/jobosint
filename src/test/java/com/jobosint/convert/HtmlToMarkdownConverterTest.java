package com.jobosint.convert;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HtmlToMarkdownConverterTest {

    @Test
    public void convertToMarkdown() {
        String markdown = new HtmlToMarkdownConverter().convertToMarkdown(HTML);
        System.out.println(markdown);
        assertTrue(markdown.contains("About the job"));
        assertTrue(markdown.contains("**Who You'll Work With**"));
        assertTrue(markdown.contains("**What You'll Do**"));
    }

    private static final String HTML = """
            <article class="jobs-description__container
                      jobs-description__container--condensed">
             <div class="jobs-description__content jobs-description-content
                        jobs-description__content--condensed">
              <div class="jobs-box__html-content jobs-description-content__text t-14 t-normal
                          jobs-description-content__text--stretch" id="job-details" tabindex="-1">
               <h2 class="text-heading-large
                            mb4">About the job</h2><!----> <span> <strong><!---->Who You’ll Work With<!----><br><br></strong><!---->Slalom Emerge is a team of trailblazers helping ensure we achieve our strategic goals of investing for the future and pursuing innovation as a competitive advantage. We empower our local markets by identifying emerging capabilities, building multi-disciplinary teams, and providing access to niche and hyper-specialized expertise.<!----><br><br><!---->As a modernized technology company, our Slalom Technologists are disrupting the market and bringing to life the art of the possible for our clients. We have passion for building strategies, solutions, and creative products to help our clients solve their most complex and interesting business problems. We surround our technologists with interesting challenges, innovative minds, and emerging technologies. As an Enterprise Architect in Slalom’s global Technology Strategy practice you will partner with clients and local markets to evaluate technology strategy, organization, business applications, infrastructure, and controls. This role will help clients evaluate and establish the business and enterprise architecture blueprint for business technology alignment to reduce costs, minimize risk, and increase value.<!----><br><br>
                <ul>
                 <li><!---->We’re a global team that supports our local markets, and we get to work with Slalom’s Fortune 500 and mid-market enterprises using the latest technology to realize their visions<!----></li>
                 <li><!---->We collaborate with our Slalom colleagues, our partners, our community, and our clients on opportunities we couldn’t have imagined<!----></li>
                 <li><!---->With mentoring and coaching at the heart of our practice we get to take budding architects under our wings and help them realize their vision of being an Enterprise Architect or a Technology Strategist.<!----></li>
                 <li><!---->Best of all, we’re forced to grow, and we're continuously challenged as technologies and our clients evolve ever faster.<!----><br><br></li>
                </ul><strong><!---->What You’ll Do<!----><br><br></strong>
                <ul>
                 <li><!---->Advise clients on the overall enterprise architecture landscape on various solution architecture areas including technology, information, data, security, and integration. Implement and architect large scale, innovative IT solutions to drive business transformation.<!----></li>
                 <li><!---->Evaluate and identify IT-related risks, including compliance, operations, revenue, and growth risks.<!----></li>
                 <li><!---->Develop, as part of a team, technology strategy and planning deliverables to address IT risks and improve effectiveness of the IT organization, specifically focusing on business application integration.<!----></li>
                 <li><!---->Produce target state solution architectures, perform architectural assessment and gap analysis and help formulate the overall solution strategy and roadmap.<!----></li>
                 <li><!---->Collaborate with clients and business analysts to translate business requirements into technical requirements.<!----></li>
                 <li><!---->Assist with estimating work efforts required for each phase of a project<!----></li>
                 <li><!---->Support growing and scaling the Technology Strategy and Enterprise Architecture practice across Slalom, including client discussions, thought leadership, coaching and mentoring junior architects and local market resources, offering development and business development.<span class="white-space-pre"> </span><br><br></li>
                </ul><strong><!---->What You’ll Bring<!----><br><br></strong>
                <ul>
                 <li><!---->Willingness to be a part of high-performing team in a diverse environment, and ability to thrive in ambiguity<!----></li>
                 <li><!---->Strong ability to share ideas and communicate with peers and clients<!----></li>
                 <li><!---->Fluency in the development and use of solution architecture frameworks, patterns, and best practices to drive business transformation. Familiarity and exposure to Enterprise Architecture frameworks and methodologies (Zachman, TOGAF, Gartner, etc.)<!----></li>
                 <li><!---->Experience in analyzing business and technology requirements to design and build scalable enterprise application architectures. Ability to explain principles of application architecture to clients using value-based frameworks<!----></li>
                 <li><!---->Knowledge of application development practices including Agile and SAFe, systems development lifecycle, and implementation of software products<!----></li>
                 <li><!---->Hands-on programming experience in various development platforms<!----></li>
                 <li><!---->Experience leading development teams through everyday project tasks and collaborating across multiple functional/technical teams to deliver a project<!----></li>
                 <li><!---->Strong written and verbal communication skills<!----></li>
                 <li><!---->Develop and coach high-performing teams by hiring diverse talent, prioritizing development, leading by example and preparing people for more senior positions in other parts of the organization (Principals only)<!----></li>
                 <li><!---->Experience working in IT leadership roles, in multiple businesses, business domains, and industries<!----></li>
                 <li><!---->However, just being smart, having some certifications, and being able to rattle off a bunch of key operations concepts won’t cut it. Slalom Enterprise Architects need to be able to establish credibility from the first meeting and continue to grow the client’s trust. They must bring together business and technology leaders and practitioners to frame a clear enterprise vision, a strategy to achieve it, and a road map to get there. They need to drive effective collaboration across disciplines, partners, and client business and technology groups to deliver that road map.<!----></li>
                 <li><!---->Feel like you’re on the path to becoming an Enterprise Architect, but you’re not quite there yet? We’d love to chat with you to see if we can take you from where you are today and grow you into a Slalom Enterprise Architect.<!----><br><br></li>
                </ul><strong><!---->About Us<!----><br><br></strong><!---->Slalom is a purpose-led, global business and technology consulting company. From strategy to implementation, our approach is fiercely human. In six countries and 43 markets, we deeply understand our customers—and their customers—to deliver practical, end-to-end solutions that drive meaningful impact. Backed by close partnerships with over 400 leading technology providers, our 13,000+ strong team helps people and organizations dream bigger, move faster, and build better tomorrows for all. We’re honored to be consistently recognized as a great place to work, including being one of Fortune’s 100 Best Companies to Work For seven years running. Learn more at slalom.com.<!----><br><br><strong><!---->Compensation And Benefits<!----><br><br></strong><!---->Slalom prides itself on helping team members thrive in their work and life. As a result, Slalom is proud to invest in benefits that include meaningful time off and paid holidays, parental leave, 401(k) with a match, a range of choices for highly subsidized health, dental, &amp; vision coverage, adoption and fertility assistance, and short/long-term disability. We also offer additional benefits such as a yearly $350 reimbursement account for any well-being related expenses as well as discounted home, auto, and pet insurance.<!----><br><br><!---->Slalom is committed to fair and equitable compensation practices. Actual compensation will depend upon an individual’s skills, experience, qualifications, location, and other relevant factors. The salary pay range is subject to change and may be modified at any time.<!----><br><br><strong><!---->EEO and Accommodations<!----><br><br></strong><em><!---->Slalom is an equal opportunity employer and is committed to inclusion, diversity, and equity in the workplace. All qualified applicants will receive consideration for employment without regard to race, color, religion, sex, national origin, disability status, protected veterans’ status, or any other characteristic protected by federal, state, or local laws. Slalom will also consider qualified applications with criminal histories, consistent with legal requirements. Slalom welcomes and encourages applications from individuals with disabilities. Reasonable accommodations are available for candidates during all aspects of the selection process. Please advise the talent acquisition team if you require accommodations during the interview process.<!----></em> <!----> </span>
              </div><!---->
              <div class="jobs-description__details">
               <!---->
              </div>
             </div>
            </article>
        """;
}

import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  ArrowRight,
  BarChart3,
  BellRing,
  Boxes,
  Building2,
  Check,
  ChefHat,
  CloudCog,
  CreditCard,
  ExternalLink,
  Globe,
  GraduationCap,
  Headphones,
  HelpCircle,
  Layout,
  Mail,
  MessageSquare,
  MapPin,
  Network,
  Phone,
  ShieldCheck,
  ShoppingBag,
  Smartphone,
  Sparkles,
  Star,
  Store,
  Tablet,
  TrendingUp,
  Truck,
  Users,
  UtensilsCrossed,
  Zap
} from 'lucide-react';

const SaaSLanding = () => {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');

  const coreModules = [
    {
      title: 'Cloud POS Engine',
      desc: 'Fast billing, table operations, split payments, and shift-level control from one counter app.',
      icon: Store
    },
    {
      title: 'Kitchen Display System',
      desc: 'Live KOT queue with prep priority, station routing, and service-time tracking.',
      icon: ChefHat
    },
    {
      title: 'Online Ordering Hub',
      desc: 'Unify dine-in, pickup, and delivery orders with centralized fulfillment flow.',
      icon: ShoppingBag
    },
    {
      title: 'Inventory Control',
      desc: 'Recipe-level stock deduction, low-stock alerts, and purchase tracking.',
      icon: Boxes
    },
    {
      title: 'Payments & Settlement',
      desc: 'Multiple payment modes, reconciliation exports, and smarter closing reports.',
      icon: CreditCard
    },
    {
      title: 'Business Intelligence',
      desc: 'Outlet-wise dashboards, menu performance, and hour-by-hour revenue insights.',
      icon: BarChart3
    },
    {
      title: 'Customer Engagement',
      desc: 'Feedback capture, repeat-customer profiles, and promotional campaign hooks.',
      icon: BellRing
    },
    {
      title: 'API & Integrations',
      desc: 'Connect delivery apps, accounting tools, CRM flows, and third-party services.',
      icon: Network
    }
  ];

  const businessModels = [
    { title: 'QSR', desc: 'Counter-first workflows with speed billing and token-ready operations.' },
    { title: 'Casual Dining', desc: 'Table mapping, waiter-assisted ordering, and smooth bill management.' },
    { title: 'Fine Dining', desc: 'Course-aware service flow, rich menu customization, and hospitality insights.' },
    { title: 'Cafe', desc: 'Compact setup with quick menu edits, combos, and loyalty-led retention.' },
    { title: 'Cloud Kitchen', desc: 'Delivery-centric control with multi-brand order orchestration.' },
    { title: 'Enterprise', desc: 'Centralized controls for outlets, brands, and regions from one console.' }
  ];

  const operationsSuite = [
    { title: 'Self-Order Kiosk Ready', icon: Tablet },
    { title: 'Waiter Mobile Workflows', icon: Smartphone },
    { title: 'Cloud Printing Support', icon: CloudCog },
    { title: 'Multi-Outlet Governance', icon: Building2 },
    { title: 'Security & Role Control', icon: ShieldCheck },
    { title: 'Onboarding & Academy', icon: GraduationCap }
  ];

  const circleModules = [
    { title: 'POS', icon: Store, x: '50%', y: '5%' },
    { title: 'KDS', icon: ChefHat, x: '80%', y: '20%' },
    { title: 'Orders', icon: ShoppingBag, x: '92%', y: '50%' },
    { title: 'Payments', icon: CreditCard, x: '80%', y: '80%' },
    { title: 'Inventory', icon: Boxes, x: '50%', y: '95%' },
    { title: 'Analytics', icon: BarChart3, x: '20%', y: '80%' },
    { title: 'CRM', icon: BellRing, x: '8%', y: '50%' },
    { title: 'Integrations', icon: Network, x: '20%', y: '20%' }
  ];

  const productGallery = [
    {
      title: 'ServeSmart Counter View',
      desc: 'Billing, order queue, and table actions in one screen.',
      image: '/food_hero_banner.png'
    },
    {
      title: 'Kitchen Operations Board',
      desc: 'Live ticket progress and prep sequencing for kitchen teams.',
      image: 'https://images.unsplash.com/photo-1556911261-6bd341186b2f?auto=format&fit=crop&q=80&w=1200'
    },
    {
      title: 'Customer Ordering Experience',
      desc: 'Fast QR ordering flow optimized for mobile customers.',
      image: '/food_placeholder.png'
    }
  ];

  return (
    <div className="min-h-screen bg-[#F8F7F2] text-gray-900 font-sans selection:bg-amber-100 selection:text-amber-900 relative overflow-x-hidden">
      {/* Navbar */}
      <nav className="fixed top-0 left-0 right-0 z-50 bg-white/85 backdrop-blur-xl border-b border-gray-100">
        <div className="max-w-7xl mx-auto px-6 h-20 flex items-center justify-between">
          <div className="flex items-center gap-2 cursor-pointer" onClick={() => navigate('/')}>
            <div className="w-10 h-10 bg-amber-500 rounded-xl flex items-center justify-center shadow-lg shadow-amber-500/20">
              <Zap size={22} className="text-white fill-white" />
            </div>
            <span className="text-2xl font-black tracking-tighter">Vitteno <span className="text-gray-400 font-serif italic text-lg ml-1">Technologies</span></span>
          </div>
          
          <div className="hidden md:flex items-center gap-10 font-bold text-sm text-gray-500">
            <a href="#features" className="hover:text-amber-600 transition">Features</a>
            <a href="#solutions" className="hover:text-amber-600 transition">Solutions</a>
            <a href="#services" className="hover:text-amber-600 transition">Services</a>
            <a href="#pricing" className="hover:text-amber-600 transition">Pricing</a>
            <a href="#support" className="hover:text-amber-600 transition">Support</a>
          </div>

          <div className="flex items-center gap-4">
            <button 
              onClick={() => navigate('/admin/login')}
              className="text-sm font-black text-gray-900 hover:text-amber-600 transition px-4 py-2"
            >
              Login
            </button>
            <button 
              onClick={() => navigate('/register')}
              className="bg-gray-900 text-white px-6 py-3 rounded-1.5xl font-black text-sm hover:scale-105 active:scale-95 transition shadow-xl shadow-gray-200"
            >
              Get Started
            </button>
          </div>
        </div>
      </nav>

      {/* Hero Section */}
      <section className="pt-40 pb-20 px-6 relative overflow-hidden bg-gradient-to-b from-white to-[#F8F7F2] section-reveal">
        <div className="absolute top-0 right-0 w-[800px] h-[800px] bg-amber-500/10 rounded-full -mr-96 -mt-96 blur-3xl landing-glow-one"></div>
        <div className="absolute bottom-0 left-0 w-[600px] h-[600px] bg-sky-300/10 rounded-full -ml-72 -mb-72 blur-3xl landing-glow-two"></div>
        <div className="max-w-7xl mx-auto grid grid-cols-1 lg:grid-cols-2 gap-20 items-center">
          <div className="relative z-10 animate-in slide-in-from-left duration-1000">
            <div className="inline-flex items-center gap-2 px-4 py-2 bg-amber-50 text-amber-700 rounded-full text-xs font-black tracking-widest uppercase mb-6 border border-amber-200">
              <Star size={12} className="fill-amber-600" /> Trusted by modern restaurant teams
            </div>
            <h1 className="text-6xl md:text-8xl font-black tracking-tight leading-[0.9] text-gray-900 mb-8">
              One Platform for <span className="text-amber-500">Restaurant</span> Growth.
            </h1>
            <p className="text-xl text-gray-500 font-medium leading-relaxed max-w-xl mb-10">
              Run POS, kitchen, delivery, inventory, payments, and outlet intelligence from a single cloud system built for hospitality.
            </p>
            
            <div className="flex flex-col sm:flex-row gap-4">
              <div className="flex-1 bg-gray-50 border-2 border-gray-100 rounded-2xl p-2 flex items-center focus-within:border-amber-500 transition shadow-sm">
                 <input 
                   type="email" 
                   placeholder="Enter your work email"
                   value={email}
                   onChange={(e) => setEmail(e.target.value)}
                   className="flex-1 bg-transparent border-0 px-4 py-3 font-bold outline-none"
                 />
                 <button
                   onClick={() => navigate('/register')}
                   className="bg-amber-500 text-white px-6 py-3 rounded-xl font-black text-sm flex items-center gap-2 hover:bg-amber-600 transition"
                 >
                   Book Demo <ArrowRight size={18} />
                 </button>
              </div>
            </div>

            <div className="mt-8 flex flex-wrap gap-3">
              {['POS + KDS', 'Online Ordering', 'Inventory', 'BI Dashboard'].map((pill) => (
                <span key={pill} className="px-4 py-2 bg-white border border-gray-200 rounded-full text-xs font-black uppercase tracking-widest text-gray-600 hover:-translate-y-1 hover:shadow-lg transition duration-300">
                  {pill}
                </span>
              ))}
            </div>
          </div>

          <div className="relative animate-in zoom-in duration-1000 delay-300">
             <div className="relative z-10 bg-white p-4 rounded-[3rem] shadow-2xl border border-gray-100 rotate-2 hover:rotate-0 transition-transform duration-700">
                <img 
                  src="https://images.unsplash.com/photo-1552566626-52f8b828add9?auto=format&fit=crop&q=80&w=1200" 
                  alt="Dashboard Preview" 
                  className="rounded-[2.5rem] shadow-inner"
                />
                <div className="absolute -bottom-10 -left-10 bg-white p-6 rounded-3xl shadow-2xl border border-gray-100 transition-all duration-1000">
                   <div className="flex items-center gap-4">
                      <div className="w-12 h-12 bg-green-100 text-green-600 rounded-2xl flex items-center justify-center font-black">
                        <Users size={24} />
                      </div>
                      <div>
                        <p className="text-xs font-black text-gray-400">LIVE OUTLETS</p>
                        <p className="text-2xl font-black text-gray-900">124+</p>
                      </div>
                   </div>
                </div>
             </div>
             <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-full h-full bg-amber-500/10 blur-3xl rounded-full -z-10"></div>
          </div>
        </div>
      </section>

      {/* All-In-One Circle */}
      <section className="py-28 px-6 bg-[#F3F4F6] section-reveal">
        <div className="max-w-7xl mx-auto grid grid-cols-1 lg:grid-cols-2 gap-14 items-center">
          <div>
            <p className="text-amber-600 text-xs font-black uppercase tracking-widest mb-3">Everything in one circle</p>
            <h2 className="text-4xl md:text-5xl font-black tracking-tight text-gray-900 mb-6">ServeSmart connects every operation in a single platform loop.</h2>
            <p className="text-lg text-gray-500 font-medium leading-relaxed mb-8">
              Orders move from counter to kitchen to delivery to reports without switching tools. Your team works in one connected flow.
            </p>
            <div className="space-y-3">
              {[
                'One source of truth for menu, staff, pricing, and reporting',
                'Instant sync between owner, admin, and kitchen screens',
                'Faster service with fewer operational handoff errors'
              ].map((line) => (
                <div key={line} className="flex items-center gap-3 text-gray-700 font-bold">
                  <div className="w-6 h-6 rounded-full bg-emerald-100 text-emerald-600 flex items-center justify-center"><Check size={14} /></div>
                  {line}
                </div>
              ))}
            </div>
          </div>
          <div className="relative h-[430px]">
            <div className="absolute inset-10 rounded-full border-[18px] border-white shadow-xl bg-gradient-to-br from-amber-50 to-white orbit-ring"></div>
            <div className="absolute inset-0 flex items-center justify-center">
              <div className="w-44 h-44 rounded-full bg-gray-900 text-white flex flex-col items-center justify-center shadow-2xl pulse-core">
                <UtensilsCrossed size={28} className="mb-2 text-amber-400" />
                <p className="text-xs font-black tracking-widest uppercase">ServeSmart</p>
                <p className="text-[10px] font-bold text-gray-300">All-in-One Core</p>
              </div>
            </div>
            {circleModules.map((module, idx) => (
              <div
                key={module.title}
                className="absolute -translate-x-1/2 -translate-y-1/2 bg-white border border-gray-200 rounded-2xl px-4 py-3 shadow-md flex items-center gap-2 orbit-module"
                style={{ left: module.x, top: module.y, animationDelay: `${idx * 160}ms` }}
              >
                <module.icon size={16} className="text-amber-600" />
                <span className="text-[10px] font-black uppercase tracking-widest text-gray-700">{module.title}</span>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Core Modules */}
      <section id="features" className="py-32 px-6 bg-gray-50 section-reveal">
        <div className="max-w-7xl mx-auto">
          <div className="text-center max-w-3xl mx-auto mb-20">
            <h2 className="text-4xl md:text-5xl font-black tracking-tight text-gray-900 mb-6">Run your restaurant your way.</h2>
            <p className="text-lg text-gray-500 font-medium leading-relaxed">
              Vitteno combines front-of-house, kitchen, and back-office into one connected operating layer.
            </p>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
            {coreModules.map((f, i) => (
              <div key={i} className="bg-white p-10 rounded-[2.5rem] shadow-sm border border-gray-100 hover:shadow-xl hover:scale-105 transition duration-500 group card-float" style={{ animationDelay: `${i * 80}ms` }}>
                <div className="w-16 h-16 bg-amber-50 text-amber-600 rounded-2xl flex items-center justify-center mb-8 group-hover:bg-amber-500 group-hover:text-white transition-colors duration-500">
                  <f.icon size={32} />
                </div>
                <h3 className="text-xl font-black text-gray-900 mb-4">{f.title}</h3>
                <p className="text-gray-500 font-medium leading-relaxed">
                  {f.desc}
                </p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Business Models */}
      <section id="solutions" className="py-28 px-6 bg-white section-reveal">
        <div className="max-w-7xl mx-auto">
          <div className="max-w-3xl mb-14">
            <p className="text-amber-600 text-xs font-black uppercase tracking-widest mb-3">Solutions by business type</p>
            <h2 className="text-4xl md:text-5xl font-black tracking-tight text-gray-900 mb-4">Built for every stage of growth.</h2>
            <p className="text-lg text-gray-500 font-medium">Choose the operating model that matches your restaurant and scale without rebuilding your stack.</p>
          </div>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {businessModels.map((model) => (
              <div key={model.title} className="p-8 rounded-[2rem] border border-gray-200 bg-[#FAFAFA] hover:bg-amber-50 hover:border-amber-200 transition">
                <h3 className="text-xl font-black text-gray-900 mb-3">{model.title}</h3>
                <p className="text-sm font-medium text-gray-500 leading-relaxed">{model.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Services Suite */}
      <section id="services" className="py-28 px-6 bg-[#0F172A] text-white relative overflow-hidden section-reveal">
        <div className="absolute inset-0 bg-[radial-gradient(circle_at_20%_20%,rgba(245,158,11,0.25),transparent_35%)]"></div>
        <div className="max-w-7xl mx-auto relative z-10">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 items-start">
            <div>
              <p className="text-amber-300 text-xs font-black uppercase tracking-widest mb-4">Service Ecosystem</p>
              <h2 className="text-4xl md:text-5xl font-black tracking-tight leading-tight mb-6">More than software. A full restaurant operating service.</h2>
              <p className="text-slate-300 text-lg font-medium leading-relaxed">
                From rollout and staff training to integrations and performance reviews, Vitteno supports your team beyond implementation.
              </p>
              <div className="mt-8 space-y-3">
                {['Implementation in days, not months', 'Role-based training for owner/admin/kitchen', 'Quarterly optimization and performance check-ins'].map((point) => (
                  <div key={point} className="flex items-center gap-3 text-slate-100 font-bold">
                    <div className="w-6 h-6 rounded-full bg-amber-400/20 text-amber-300 flex items-center justify-center"><Check size={14} /></div>
                    {point}
                  </div>
                ))}
              </div>
            </div>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              {operationsSuite.map((service) => (
                <div key={service.title} className="p-6 rounded-2xl border border-white/10 bg-white/5 backdrop-blur-sm">
                  <service.icon size={22} className="text-amber-300 mb-4" />
                  <p className="font-black text-sm tracking-wide">{service.title}</p>
                </div>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* Pricing Section */}
      <section id="pricing" className="py-32 px-6 bg-white overflow-hidden relative section-reveal">
        <div className="max-w-7xl mx-auto">
          <div className="text-center max-w-3xl mx-auto mb-20">
            <h2 className="text-5xl font-black tracking-tight text-gray-900 mb-6">Simple, Transparent Pricing.</h2>
            <p className="text-lg text-gray-500 font-medium leading-relaxed">
              No hidden fees. Choose a plan that matches your restaurant's stage of growth.
            </p>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8 relative z-10">
            {[
              { 
                name: 'Starter', 
                price: '1,099', 
                desc: 'Perfect for local cafes starting their digital journey.',
                features: ['Single Outlet Access', 'Digital QR Menu', 'Manual Order Entry', 'Basic Sales Reports'],
                color: 'bg-gray-50',
                id: 'STARTER',
                btnClass: 'bg-gray-900 text-white'
              },
              { 
                name: 'Classic', 
                price: '1,499', 
                desc: 'Best for growing restaurants with high order volumes.',
                features: ['Upto 3 Outlets', 'Real-time Kitchen Display', 'Inventory Management', 'Customer Feedback System'],
                color: 'bg-amber-50 border-amber-200',
                id: 'CLASSIC',
                btnClass: 'bg-amber-500 text-white shadow-xl shadow-amber-200',
                popular: true
              },
              { 
                name: 'Premium', 
                price: '2,499', 
                desc: 'Enterprise-grade features for multi-city restaurant chains.',
                features: ['Unlimited Outlets', 'Advanced Analytics Pro', 'Multi-tenant HQ Access', 'Dedicated Account Manager'],
                color: 'bg-gray-900 text-white',
                id: 'PREMIUM',
                btnClass: 'bg-white text-gray-900'
              }
            ].map((plan, i) => (
              <div key={i} className={`p-12 rounded-[3rem] border transition-all hover:scale-105 duration-500 flex flex-col relative ${plan.color}`}>
                {plan.popular && (
                  <span className="absolute -top-4 left-1/2 -translate-x-1/2 bg-amber-500 text-white px-4 py-1.5 rounded-full text-[10px] font-black uppercase tracking-widest shadow-lg">Most Popular</span>
                )}
                <h3 className="text-2xl font-black mb-2">{plan.name}</h3>
                <p className={`text-sm mb-8 font-medium ${plan.name === 'Premium' ? 'text-gray-400' : 'text-gray-500'}`}>{plan.desc}</p>
                <div className="flex items-baseline gap-1 mb-8">
                  <span className="text-5xl font-black tracking-tighter">₹{plan.price}</span>
                  <span className={`text-sm font-bold ${plan.name === 'Premium' ? 'text-gray-400' : 'text-gray-500'}`}>/month</span>
                </div>
                <div className="space-y-4 mb-12 flex-1">
                  {plan.features.map((feat, fi) => (
                    <div key={fi} className="flex items-center gap-3">
                      <div className={`w-5 h-5 rounded-full flex items-center justify-center flex-shrink-0 ${plan.name === 'Premium' ? 'bg-white/10 text-white' : 'bg-amber-100 text-amber-600'}`}>
                        <Check size={12} />
                      </div>
                      <span className="text-sm font-bold opacity-80">{feat}</span>
                    </div>
                  ))}
                </div>
                <button 
                  onClick={() => navigate(`/register?plan=${plan.id}`)}
                  className={`w-full py-5 rounded-2.5xl font-black text-sm transition active:scale-95 ${plan.btnClass}`}
                >
                  Choose {plan.name}
                </button>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Support & Enablement */}
      <section id="support" className="py-28 px-6 bg-gray-50 section-reveal">
        <div className="max-w-7xl mx-auto">
          <div className="text-center max-w-3xl mx-auto mb-14">
            <p className="text-amber-600 text-xs font-black uppercase tracking-widest mb-3">Support that scales with you</p>
            <h2 className="text-4xl md:text-5xl font-black tracking-tight text-gray-900 mb-5">Resources for teams, not just tech teams.</h2>
            <p className="text-lg text-gray-500 font-medium">Get guided onboarding, knowledge base help, and practical academy modules for daily operations.</p>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="bg-white p-8 rounded-[2rem] border border-gray-200">
              <Headphones size={26} className="text-amber-500 mb-4" />
              <h3 className="text-xl font-black text-gray-900 mb-2">Priority Support</h3>
              <p className="text-sm text-gray-500 font-medium leading-relaxed">Fast issue resolution for billing, orders, printers, and integration incidents.</p>
            </div>
            <div className="bg-white p-8 rounded-[2rem] border border-gray-200">
              <GraduationCap size={26} className="text-amber-500 mb-4" />
              <h3 className="text-xl font-black text-gray-900 mb-2">Vitteno Academy</h3>
              <p className="text-sm text-gray-500 font-medium leading-relaxed">Role-based tutorials for owners, managers, and kitchen teams.</p>
            </div>
            <div className="bg-white p-8 rounded-[2rem] border border-gray-200">
              <ExternalLink size={26} className="text-amber-500 mb-4" />
              <h3 className="text-xl font-black text-gray-900 mb-2">Knowledge Base</h3>
              <p className="text-sm text-gray-500 font-medium leading-relaxed">Step-by-step docs for onboarding, menu setup, payments, and operations.</p>
            </div>
          </div>
        </div>
      </section>

      {/* Product Gallery */}
      <section className="py-28 px-6 bg-white section-reveal">
        <div className="max-w-7xl mx-auto">
          <div className="text-center max-w-3xl mx-auto mb-14">
            <p className="text-amber-600 text-xs font-black uppercase tracking-widest mb-3">ServeSmart Product Views</p>
            <h2 className="text-4xl md:text-5xl font-black tracking-tight text-gray-900 mb-5">See what your team uses every day.</h2>
            <p className="text-lg text-gray-500 font-medium">Operational screens built for speed, clarity, and control across the restaurant floor.</p>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            {productGallery.map((item) => (
              <article key={item.title} className="bg-gray-50 rounded-[2rem] overflow-hidden border border-gray-200 hover:shadow-xl transition group card-float">
                <img src={item.image} alt={item.title} className="h-52 w-full object-cover group-hover:scale-105 transition duration-700" />
                <div className="p-6">
                  <h3 className="text-lg font-black text-gray-900 mb-2">{item.title}</h3>
                  <p className="text-sm text-gray-500 font-medium leading-relaxed">{item.desc}</p>
                </div>
              </article>
            ))}
          </div>
        </div>
      </section>

      {/* Statistics / Social Proof */}
      <section className="py-32 px-6 bg-white overflow-hidden relative section-reveal">
        <div className="max-w-7xl mx-auto">
           <div className="grid grid-cols-1 lg:grid-cols-2 gap-20 items-center">
              <div>
                 <h2 className="text-4xl font-black tracking-tight text-gray-900 mb-8 leading-tight">The operating stack for modern hospitality.</h2>
                 <div className="space-y-4">
                    {[
                      'Unified order pipeline across dine-in, pickup, and delivery.',
                      'Real-time revenue and outlet performance visibility.',
                      'Role-based access control with multi-tenant data isolation.'
                    ].map((item, i) => (
                      <div key={i} className="flex items-center gap-4 text-lg font-bold text-gray-700">
                         <div className="w-6 h-6 rounded-full bg-emerald-100 text-emerald-600 flex items-center justify-center flex-shrink-0">
                            <Check size={14} />
                         </div>
                         {item}
                      </div>
                    ))}
                 </div>
                 <button 
                  onClick={() => navigate('/register')}
                  className="mt-12 bg-gray-900 text-white px-10 py-5 rounded-2.5xl font-black text-lg hover:scale-105 transition shadow-2xl shadow-gray-200 flex items-center gap-3 group"
                 >
                  Start Your Rollout <ArrowRight size={24} className="group-hover:translate-x-2 transition" />
                 </button>
              </div>
              <div className="grid grid-cols-2 gap-6 relative">
                 <div className="space-y-6">
                    <div className="bg-amber-50 p-8 rounded-[2.5rem] text-center border border-amber-100">
                       <p className="text-4xl font-black text-amber-600 mb-1">99.9%</p>
                       <p className="text-[10px] font-black text-amber-800 uppercase tracking-widest">Uptime Promise</p>
                    </div>
                    <div className="bg-gray-900 p-8 rounded-[2.5rem] text-center text-white">
                       <p className="text-4xl font-black text-white mb-1">220k+</p>
                       <p className="text-[10px] font-black text-gray-400 uppercase tracking-widest">Monthly Orders</p>
                    </div>
                 </div>
                 <div className="space-y-6 pt-12">
                    <div className="bg-blue-50 p-8 rounded-[2.5rem] text-center border border-blue-100">
                       <p className="text-4xl font-black text-blue-600 mb-1">12+</p>
                       <p className="text-[10px] font-black text-blue-800 uppercase tracking-widest">Service Modules</p>
                    </div>
                    <div className="bg-emerald-50 p-8 rounded-[2.5rem] text-center border border-emerald-100">
                       <p className="text-4xl font-black text-emerald-600 mb-1">24/7</p>
                       <p className="text-[10px] font-black text-emerald-800 uppercase tracking-widest">Operational Visibility</p>
                    </div>
                 </div>
                 <div className="absolute inset-0 bg-amber-500/5 blur-3xl -z-10 rounded-full"></div>
              </div>
           </div>
        </div>
      </section>

      {/* Closing CTA */}
      <section className="py-24 px-6 bg-[#111827] section-reveal">
        <div className="max-w-5xl mx-auto text-center cta-aurora rounded-[2.5rem] p-10 md:p-14 border border-white/10">
          <p className="text-amber-300 text-xs font-black uppercase tracking-widest mb-4">Ready to go live</p>
          <h2 className="text-white text-4xl md:text-5xl font-black tracking-tight mb-6">Launch your all-in-one restaurant system with Vitteno.</h2>
          <p className="text-slate-300 font-medium text-lg mb-10">From first outlet to regional scale, we help you standardize operations and grow confidently.</p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <button
              onClick={() => navigate('/register')}
              className="px-8 py-4 rounded-2xl bg-amber-500 text-white font-black hover:bg-amber-600 transition"
            >
              Start Free Trial
            </button>
            <button
              onClick={() => window.location.href = 'mailto:contact@servesmart.in'}
              className="px-8 py-4 rounded-2xl bg-white text-gray-900 font-black hover:bg-gray-100 transition flex items-center justify-center gap-2"
            >
              Contact Us <Mail size={16} />
            </button>
          </div>
        </div>
      </section>

      <footer className="py-20 px-6 border-t border-gray-100 bg-[#0B1020] text-white section-reveal">
        <div className="max-w-7xl mx-auto grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-10">
          <div className="lg:col-span-2">
            <div className="flex items-center gap-2 mb-4">
              <div className="w-9 h-9 bg-amber-500 rounded-lg flex items-center justify-center">
                <Zap size={18} className="text-white fill-white" />
              </div>
              <span className="text-2xl font-black tracking-tighter">ServeSmart</span>
            </div>
            <p className="text-slate-300 font-medium text-sm max-w-md leading-relaxed mb-6">
              ServeSmart is a cloud-first restaurant operating platform for POS, kitchen, delivery, inventory, and business intelligence.
            </p>
            <div className="flex items-center gap-3 text-slate-300 text-sm">
              <MapPin size={14} /> Chennai, India
            </div>
          </div>

          <div>
            <h4 className="text-sm font-black uppercase tracking-widest text-amber-300 mb-4">Products</h4>
            <ul className="space-y-2 text-sm text-slate-300 font-medium">
              <li>Cloud POS</li>
              <li>Kitchen Display</li>
              <li>Online Ordering</li>
              <li>Inventory Suite</li>
              <li>Analytics Dashboard</li>
            </ul>
          </div>

          <div>
            <h4 className="text-sm font-black uppercase tracking-widest text-amber-300 mb-4">Services</h4>
            <ul className="space-y-2 text-sm text-slate-300 font-medium">
              <li>Implementation</li>
              <li>Training & Onboarding</li>
              <li>Integrations</li>
              <li>Support</li>
              <li>Performance Review</li>
            </ul>
          </div>

          <div>
            <h4 className="text-sm font-black uppercase tracking-widest text-amber-300 mb-4">Contact</h4>
            <ul className="space-y-3 text-sm text-slate-300 font-medium">
              <li className="flex items-center gap-2"><Mail size={14} /> contact@servesmart.in</li>
              <li className="flex items-center gap-2"><Phone size={14} /> +91 90000 00000</li>
              <li className="flex items-center gap-2"><HelpCircle size={14} /> 24/7 Helpdesk</li>
            </ul>
          </div>
        </div>

        <div className="max-w-7xl mx-auto mt-12 pt-6 border-t border-white/10 flex flex-col md:flex-row items-center justify-between gap-4">
          <p className="text-xs text-slate-400 font-bold">© 2026 ServeSmart by Vitteno Technologies. All rights reserved.</p>
          <div className="flex items-center gap-6 text-xs text-slate-300 font-bold uppercase tracking-widest">
            <button className="hover:text-white transition">Privacy Policy</button>
            <button className="hover:text-white transition">Terms</button>
            <button className="hover:text-white transition">Knowledge Base</button>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default SaaSLanding;
